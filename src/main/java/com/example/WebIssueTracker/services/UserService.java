package com.example.WebIssueTracker.services;

import com.example.WebIssueTracker.models.UserModel;
import com.example.WebIssueTracker.repo.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final static String U_N_F_M =
            "User with username: %s not found";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByNickname(username)
                .orElseThrow(()-> new UsernameNotFoundException(String.format(U_N_F_M, username)));
    }

    public void registerUser(UserModel user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
    }

    public boolean checkAuthentication() {
        return  SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof AnonymousAuthenticationToken);
    }

    public boolean userExist(String nickname) {
        Optional<UserModel> user = userRepository.findUserByNickname(nickname);
        if (user.isPresent()) return true;
        return false;
    }
}
