package com.app.messaging.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.messaging.domain.NormalUser;
import com.app.messaging.domain.User;

public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM \"user\" WHERE dtype = 'NormalUser'", nativeQuery = true)
    List<NormalUser> findAllNormalUsers();
}

