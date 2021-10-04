package com.example.WebIssueTracker.services;

import com.example.WebIssueTracker.models.IssueModel;
import com.example.WebIssueTracker.repo.IssueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private static final int PAGE_SIZE = 12;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public Page<IssueModel> listIssues(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, PAGE_SIZE);
        return issueRepository.findAll(pageable);
    }

    public Page<IssueModel> searchIssues(int pageNum, String filter, String keyword) {
        Pageable pageable = PageRequest.of(pageNum - 1, PAGE_SIZE);
        System.out.println(pageNum + " " + keyword + " " + filter);
         switch (filter) {
            case "all":
                System.out.println(issueRepository.findByKeyword(keyword,pageable).getContent());
                return issueRepository.findByKeyword(keyword, pageable);
            case "author":
                System.out.println(issueRepository.findByAuthor(keyword,pageable).getContent());
                return issueRepository.findByAuthor(keyword, pageable);
            case "name":
                System.out.println(issueRepository.findByName(keyword,pageable).getContent());
                return issueRepository.findByName(keyword, pageable);
        }
        System.out.println(issueRepository.findByDescription(keyword,pageable).getContent());
        return issueRepository.findByDescription(keyword, pageable);
    }

    public IssueModel getIssueByName(String name) {
        Optional<IssueModel> issue_container = issueRepository.findIssueModelByName(name);
        return issue_container.isPresent() ? issue_container.get() : null;
    }
}
