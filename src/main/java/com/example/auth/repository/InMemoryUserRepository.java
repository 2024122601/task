package com.example.auth.repository;

import com.example.auth.domain.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> storage = new HashMap<>();
    private long idSequence = 0L;

    @Override
    public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++idSequence);
        }
        storage.put(user.getId(), user);
        return user;
    }

    public void clear() {
        storage.clear();
        idSequence = 0L;
    }
}
