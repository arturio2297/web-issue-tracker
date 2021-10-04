package com.example.WebIssueTracker.models;

import com.example.WebIssueTracker.enums.AppRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

import static com.example.WebIssueTracker.enums.AppRole.USER;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username should not be empty")
    @Size(
            min = 5,
            max = 20,
            message = "Username must have length between {min} and {max}"
    )
    @Column(unique = true, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppRole role = USER;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @NotBlank( message = "First name should not be empty")
    private String firstName;

    @Column(nullable = false)
    @NotBlank( message = "Last name should not be empty")
    private String lastName;

    public UserModel(String firstName,
                     String lastName,
                     String nickname,
                     String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_"+this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
