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
         switch (filter) {
            case "all":
                return issueRepository.findByKeyword(keyword, pageable);
            case "author":
                return issueRepository.findByAuthor(keyword, pageable);
            case "name":
                return issueRepository.findByName(keyword, pageable);
        }
        return issueRepository.findByDescription(keyword, pageable);
    }

    public IssueModel getIssueByName(String name) {
        Optional<IssueModel> issue_container = issueRepository.findIssueModelByName(name);
        return issue_container.isPresent() ? issue_container.get() : null;
    }
}
