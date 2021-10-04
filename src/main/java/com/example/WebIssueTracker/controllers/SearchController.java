package com.example.WebIssueTracker.controllers;

import com.example.WebIssueTracker.models.IssueModel;
import com.example.WebIssueTracker.services.IssueService;
import com.example.WebIssueTracker.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final UserService userService;
    private final IssueService issueService;

    public SearchController(UserService userService, IssueService issueService) {
        this.userService = userService;
        this.issueService = issueService;
    }

    @GetMapping("/search")
    public String mainSearchPage(@RequestParam(value = "filter") String f,
                                 @RequestParam(value = "keyword") String k,
                                 Model model) {
        return viewSearchPage(f,k,1,model);
    }

    @GetMapping("search/{f}/{k}/page/{pageNum}")
    public String viewSearchPage(@PathVariable(name = "f") String f,
                         @PathVariable(name = "k") String k,
                         @PathVariable(name = "pageNum") int pageNum,
                         Model model) {
        Long user_id = -1L;
        if (userService.checkAuthentication()) {
            user_id = userService.getCurrentUser().getId();
        }
        Page<IssueModel> page = issueService.searchIssues(pageNum,f,k);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("issues", page.getContent());
        model.addAttribute("search","yes");
        model.addAttribute("f",f);
        model.addAttribute("k",k);
        return "main_page";
    }
}
