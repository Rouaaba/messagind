package com.app.messaging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.app.messaging.repo.UserRepo;
import com.app.messaging.domain.Admin;
import com.app.messaging.domain.NormalUser;
import com.app.messaging.domain.User;


@Service
public class UserService {
    
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Page<User> getAllUsers(Admin admin, int page, int size){
        if(isAdmin(admin)) {
            return userRepo.findAll(PageRequest.of(page, size));
        }
        else{
            throw new SecurityException("Access denied: Only admins can view all users.");
        }
    }

    public boolean isAdmin (User user){
        return user instanceof Admin;
    }

    public User getUserById(int id){
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public Admin createAdmin(Admin admin) {
        return userRepo.save(admin);
    }
    

    public NormalUser createUser(String username, String email, int adminId) {
        User admin = userRepo.findById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!(admin instanceof Admin)) {
            throw new SecurityException("Access denied: Only admins can create users.");
        }
        
        NormalUser user = new NormalUser();
        user.setUsername(username);
        user.setEmail(email);
        
        return userRepo.save(user); // Ensure userRepo is capable of handling NormalUser objects
    }    
}
