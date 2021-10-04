package com.example.WebIssueTracker.repo;

import com.example.WebIssueTracker.models.IssueModel;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;


@Repository
public interface IssueRepository extends JpaRepository<IssueModel, Long> {

    @Query(value = "select * from issue_model where author like %:keyword%" +
            " or description like %:keyword%" +
            " or name like %:keyword% order by id desc", nativeQuery = true)
    Page<IssueModel> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select * from issue_model where author like %:keyword% order by id desc", nativeQuery = true)
    Page<IssueModel> findByAuthor(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select * from issue_model where name like %:keyword% order by id desc", nativeQuery = true)
    Page<IssueModel> findByName(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select * from issue_model where description like %:keyword% order by id desc", nativeQuery = true)
    Page<IssueModel> findByDescription(@Param("keyword") String keyword, Pageable pageable);

    Optional<IssueModel> findIssueModelByName(String name);

    @Query(value = "select * from issue_model order by id desc", nativeQuery = true)
    Page<IssueModel> findAll(Pageable pageable);

}
