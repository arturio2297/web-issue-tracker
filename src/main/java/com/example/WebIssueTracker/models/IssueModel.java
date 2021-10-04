package com.example.WebIssueTracker.models;

import com.example.WebIssueTracker.enums.IssueStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.WebIssueTracker.enums.IssueStatus.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class IssueModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Title of issue should not be empty")
    @Size(
            min = 5,
            max = 25,
            message = "Title of issue must have length between {min} and {max}"
    )
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Description of issue should not be empty")
    @Size(
            min = 20,
            message = "Description of issue must have length more than {min}"
    )
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String createdDate;

    private String lastUpdateDate;

    @Enumerated(EnumType.STRING)
    private IssueStatus status = Created;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "user_id"
    )
    private UserModel user;

    @Transient
    private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss";

    public void setLastUpdateDate() {
        this.lastUpdateDate = new SimpleDateFormat(DATE_FORMAT).
                format(new Date());
    }

    public void setCreatedDate() {
        this.createdDate = new SimpleDateFormat(DATE_FORMAT).
                format(new Date());
    }
}
