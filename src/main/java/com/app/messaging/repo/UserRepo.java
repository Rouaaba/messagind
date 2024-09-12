package com.app.messaging.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.messaging.domain.User;

public interface UserRepo extends JpaRepository<User, Integer>{

}
