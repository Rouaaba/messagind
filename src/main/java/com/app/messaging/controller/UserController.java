package com.app.messaging.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.messaging.service.UserService;
import com.app.messaging.domain.Admin;
import com.app.messaging.domain.NormalUser;
import com.app.messaging.domain.User;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public Page<User> getAllUsers(@RequestParam int page, @RequestParam int size, @RequestParam int adminId) {
        // Retrieve the Admin object by the adminId
        Admin admin = (Admin) userService.getUserById(adminId);

        // Check if the user is an admin and throw an exception if not
        if (admin == null || !userService.isAdmin(admin)) {
            throw new SecurityException("Access denied: Only admins can view all users.");
        }

        // Pass the admin object, page, and size to the service method
        return userService.getAllUsers(admin, page, size);
    }

    @PostMapping("/{adminId}/create")
    public ResponseEntity<NormalUser> createUser(
            @PathVariable int adminId,
            @RequestBody NormalUser newUser) {
        
        // Validate the incoming NormalUser object
        if (newUser.getUsername() == null || newUser.getEmail() == null) {
            throw new IllegalArgumentException("Username and email must be provided.");
        }

        // Create the user using the service
        NormalUser createdUser = userService.createUser(newUser.getUsername(), newUser.getEmail(), adminId);

        // Build the URI for the created user
        URI location = URI.create("/users/" + createdUser.getId());

        // Return the response with the created user and its location
        return ResponseEntity.created(location).body(createdUser);
    }


    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin createdAdmin = userService.createAdmin(admin);
        URI location = URI.create("/admins/" + createdAdmin.getId());
        return ResponseEntity.created(location).body(createdAdmin);
    }


}

