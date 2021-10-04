package com.example.WebIssueTracker.repo;

import com.example.WebIssueTracker.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findUserByNickname(String username);

}
