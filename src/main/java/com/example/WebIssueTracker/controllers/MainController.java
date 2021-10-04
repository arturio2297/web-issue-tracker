package com.example.WebIssueTracker.controllers;

import com.example.WebIssueTracker.enums.IssueStatus;
import com.example.WebIssueTracker.models.CommentModel;
import com.example.WebIssueTracker.models.IssueModel;
import com.example.WebIssueTracker.models.UserModel;
import com.example.WebIssueTracker.repo.CommentRepository;
import com.example.WebIssueTracker.repo.IssueRepository;
import com.example.WebIssueTracker.services.ClearService;
import com.example.WebIssueTracker.services.IssueService;
import com.example.WebIssueTracker.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.WebIssueTracker.enums.IssueStatus.*;

@Controller
public class MainController {

    private final UserService userService;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final IssueService issueService;
    private final ClearService clearService;

    public MainController(IssueRepository issueRepository,
                          UserService userService,
                          CommentRepository commentRepository,
                          IssueService issueService,
                          ClearService clearService) {
        this.issueRepository = issueRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.issueService = issueService;
        this.clearService = clearService;
    }


    @GetMapping("/page/{pageNum}")
    public String viewPage(Model model,
                           @PathVariable(name = "pageNum") int pageNum) {
        Page<IssueModel> page = issueService.listIssues(pageNum);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("issues", page.getContent());
        model.addAttribute("search","no");
        Long user_id = -1L;
        if (userService.checkAuthentication()) {
            user_id = userService.getCurrentUser().getId();
        }
        model.addAttribute("user_id", user_id);
        return "main_page";
    }


    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("issues", issueRepository.findAll());
        return viewPage(model, 1);
    }

    @GetMapping("/reset")
    public String resetSearchIssues(Model model) {
        return mainPage(model);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/add")
    public String getAddIssuesPage(Model model) {
        model.addAttribute("issue", new IssueModel());
        return "add_issue";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/add")
    public String addIssue(@Valid @ModelAttribute("issue") IssueModel issue,
                           BindingResult bindingResult,
                           Model model) {
        String clear_desc = clearService.sanitizeHTML(issue.getDescription());
        if (clear_desc.length() < 20 && issue.getDescription().length() >= 20)
            bindingResult.addError(new FieldError("issue", "description",
                    "The message must not contain potentially dangerous html"));
        if (issueService.getIssueByName(issue.getName()) != null)
            bindingResult.addError(new FieldError("issue", "name",
                    "Issue with the same title already exist on the tracker"));
        if (bindingResult.hasErrors()) {
            return "add_issue";
        }
        UserModel currentUser = userService.getCurrentUser();
        issue.setAuthor(currentUser.toString());
        issue.setUser(currentUser);
        issue.setDescription(clear_desc);
        issue.setCreatedDate();
        issueRepository.save(issue);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/edit/{id}")
    public String getEditIssuesPage(@PathVariable(value = "id") Long id,
                                    Model model) {
        if (!issueRepository.existsById(id)) return "redirect:/";
        IssueModel issue = issueRepository.getById(id);
        if (!issue.getStatus().equals(Created)) return "redirect:/";
        if (!issue.getUser().equals(userService.getCurrentUser())) return "redirect:/";
        model.addAttribute("issue", issue);
        return "edit_issue";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/edit/{id}")
    public String editIssue(@PathVariable(name = "id") Long id,
                            @Valid @ModelAttribute("issue") IssueModel issue,
                            BindingResult bindingResult,
                            Model model) {
        if (!issueRepository.existsById(id)) return "redirect:/";
        IssueModel existed_issue = issueRepository.getById(id);
        if (!existed_issue.getUser().equals(userService.getCurrentUser())) return "redirect:/";
        if (!existed_issue.getStatus().equals(Created)) return "redirect:/";
        String clear_desc = clearService.sanitizeHTML(issue.getDescription());
        if (clear_desc.length() < 20 && issue.getDescription().length() >= 20)
            bindingResult.addError(new FieldError("issue", "description",
                    "The message must not contain potentially dangerous html"));
        if (bindingResult.hasErrors()) {
            return "edit_issue";
        }
        existed_issue.setName(issue.getName());
        existed_issue.setDescription(clear_desc);
        existed_issue.setLastUpdateDate();
        issueRepository.save(existed_issue);
        return "redirect:/";
    }

    @GetMapping("/detail/{id}")
    public String issueDetail(@PathVariable(value = "id") Long id,
                              Model model) {
        if (!issueRepository.existsById(id)) return "redirect:/";
        model.addAttribute("issue", issueRepository.getById(id));
        model.addAttribute("comments", commentRepository.getByIssueId(id));
        model.addAttribute("comment", new CommentModel());
        return "issue_detail";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/add-comment/{id}")
    public String commentIssue(@PathVariable(value = "id") Long id,
                               @Valid @ModelAttribute("comment") CommentModel comment,
                               BindingResult bindingResult,
                               Model model) {
        IssueModel issue = issueRepository.getById(id);
        if (!issue.getStatus().equals(Created)) return "redirect:/detail/"+id;
        String clear_context = clearService.sanitizeHTML(comment.getContext());
        if (comment.getContext().length() >= 20 && clear_context.length() < 20)
            bindingResult.addError(new FieldError("comment","context",
                    "The message must not contain potentially dangerous html"));
        if (bindingResult.hasErrors()) {
            model.addAttribute("issue",issue);
            model.addAttribute("comments",commentRepository.getByIssueId(id));
            return "issue_detail"; }
        UserModel user = userService.getCurrentUser();
        commentRepository.save(new CommentModel(
                clear_context,
                user.toString(),
                issue
        ));
        return "redirect:/detail/"+id;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("detail/{id}/change-status")
    public String commentIssue(@PathVariable(value = "id") Long id,
                               @RequestParam IssueStatus status) {
        IssueModel issue = issueRepository.getById(id);
        issue.setStatus(status);
        issue.setLastUpdateDate();
        issueRepository.save(issue);
        return "redirect:/detail/"+id;
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteIssue(@PathVariable(value = "id") Long id) {
        issueRepository.deleteById(id);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/detail/{id-issue}/delete/{id-comment}")
    public String deleteComment(@PathVariable(name = "id-issue") Long issue_id,
                              @PathVariable(name = "id-comment") Long comment_id) {
        commentRepository.deleteById(comment_id);
        return "redirect:/detail/" + issue_id;
    }
}
