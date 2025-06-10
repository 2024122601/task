package com.example.auth.repository;

import com.example.auth.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    User save(User user);
    void clear();
}
