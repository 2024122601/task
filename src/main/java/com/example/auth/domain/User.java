package com.example.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password; // 암호화 저장
    private String nickname;
    private Set<Role> roles = new HashSet<>();

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.roles.add(Role.USER);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }
}
