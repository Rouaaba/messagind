package com.app.messaging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import com.app.messaging.domain.User;
import com.app.messaging.repo.UserRepo;
import com.app.messaging.domain.Admin;
import com.app.messaging.domain.NormalUser;

@Service
public class UserService {
    
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    public Page<User> getAllUsers(int page, int size) {
        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userRepo.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (currentUser instanceof Admin) {
                return userRepo.findAll(PageRequest.of(page, size));
            } else {
                throw new SecurityException("Access denied: Only admins can view all users.");
            }
        } else {
            throw new SecurityException("Access denied: No authenticated user found.");
        }
    }
    

    public boolean isAdmin (User user){
        return user instanceof Admin;
    }

    //get user by id
    public User getUserById(int id){
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    


    //admin deletes an account
    public void deleteUserById(int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userRepo.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
    
            if (isAdmin(currentUser)) {
                User userToDelete = userRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
                userRepo.delete(userToDelete);
            } else {
                throw new SecurityException("Access denied: Only admins can delete users.");
            }
        } else {
            throw new RuntimeException("No authenticated user found");
        }
    }    


    //user deletes his account
    public void deleteCurrentUser(String username, String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userRepo.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
    
            if (username.equals(currentUser.getUsername()) && passwordEncoder.matches(password, currentUser.getPassword())) {
                userRepo.delete(currentUser);
            } else {
                throw new SecurityException("Invalid username or password");
            }
        } else {
            throw new RuntimeException("No authenticated user found");
        }
    }
    

    public Admin createAdmin(Admin admin) {
        return userRepo.save(admin);
    }
    
    

    // admin creates a normal user
    public NormalUser createUser(NormalUser newUser) {
        // Encode the password and set it
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepo.save(newUser);
    }

    
    // update infos based on id (either admin or normal user)
    public User updateUser(int id, String input){
        User user=userRepo.findById(id).orElseThrow(()-> new RuntimeException("User not found!"));
        if (input.contains("@")){
            user.setEmail(input);
        }else{
            user.setUsername(input);
        }
        return userRepo.save(user);
    }


    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("Authentication object: " + authentication); // Debugging line
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                System.out.println("Authenticated user: " + userDetails.getUsername()); // Debugging line
                return userRepo.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            }
        }
        throw new RuntimeException("No authenticated user found");
    }
    
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // Function to check if the raw password matches the encoded one
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    

}

