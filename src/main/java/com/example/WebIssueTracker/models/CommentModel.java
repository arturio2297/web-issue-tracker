package com.example.WebIssueTracker.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Comment should not be empty")
    @Column(nullable = false)
    private String context;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String createdDate;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "issue_id"
    )
    private IssueModel issue;

    @Transient
    private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss";


    public CommentModel(String context,
                        String author,
                        IssueModel issue) {
        this.context = context;
        this.author = author;
        this.createdDate = new SimpleDateFormat(DATE_FORMAT).
                format(new Date());
        this.issue = issue;
    }
}
