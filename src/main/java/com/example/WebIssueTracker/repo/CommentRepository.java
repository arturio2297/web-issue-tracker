package com.example.WebIssueTracker.repo;

import com.example.WebIssueTracker.models.CommentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long> {

    @Query(value = "select * from comment_model where issue_id = :issue_id order by id desc", nativeQuery = true)
    List<CommentModel> getByIssueId(@Param("issue_id") Long issue_id);

}
