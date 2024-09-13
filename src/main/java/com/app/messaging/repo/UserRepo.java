package com.app.messaging.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.messaging.domain.User;

public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}

