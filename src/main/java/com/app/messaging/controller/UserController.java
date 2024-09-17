package com.app.messaging.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.messaging.service.MessageService;
import com.app.messaging.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import com.app.messaging.domain.AuthResponse;
import com.app.messaging.domain.Admin;
import com.app.messaging.domain.Message;
import com.app.messaging.domain.MessageRequest;
import com.app.messaging.domain.NormalUser;
import com.app.messaging.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);



    @Autowired
    public UserController(UserService userService,  AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager= authenticationManager;
    }


    @GetMapping("users/all")
    public Page<User> getAllUsers(@RequestParam int page, @RequestParam int size) {
        return userService.getAllUsers(page, size);
    }


    @PostMapping("users/create")
    public ResponseEntity<?> createUser(@RequestBody NormalUser user) {
        try {
            userService.createUser(user);
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            // Log the error and provide a meaningful response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Log the authorities
        System.out.println("Authorities: " + userDetails.getAuthorities());

        // Extract the role from the authorities
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER"); // Default role if none found

        // Include role in the response
        return ResponseEntity.ok(new AuthResponse(role, true)); // Ensure this matches the frontend expectation
    }



    @PostMapping("/admin/create")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin createdAdmin = userService.createAdmin(admin);
        URI location = URI.create("/admins/" + createdAdmin.getId());
        return ResponseEntity.created(location).body(createdAdmin);
    }
    

    @PostMapping("users/update")
public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> payload) {
    try {
        Integer id = null;
        String input = null;

        if (payload.size() != 2) {
            return ResponseEntity.badRequest().body("Invalid payload: exactly two fields are required.");
        }

        if (payload.get("id") instanceof Integer) {
            id = (Integer) payload.get("id");
        } else {
            return ResponseEntity.badRequest().body("Invalid field id, must be an integer");
        }

        if (payload.get("input") instanceof String) {
            input = (String) payload.get("input");
        } else {
            return ResponseEntity.badRequest().body("Invalid field: input must be a string, either a username or an email!");
        }

        User updatedUser = userService.updateUser(id, input);
        return ResponseEntity.ok(updatedUser);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user: " + e.getMessage());
    }
}


    @GetMapping("/user/current")
public ResponseEntity<User> getCurrentUser() {
    User user = userService.getCurrentAuthenticatedUser();
    if (user == null) {
        throw new RuntimeException("No authenticated user found");
    }
    return ResponseEntity.ok(user);
}


    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecurityContextHolder.clearContext();
        }
        return ResponseEntity.ok("Logout successful");
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/current")
    public ResponseEntity<Void> deleteCurrentUser(@RequestParam String username, @RequestParam String password) {
        userService.deleteCurrentUser(username, password);
        return ResponseEntity.noContent().build();
    }




    @PostMapping("/encode-password")
    public ResponseEntity<String> encodePassword(@RequestParam String rawPassword) {
        String encodedPassword = userService.encodePassword(rawPassword);
        return ResponseEntity.ok(encodedPassword);
    }
 
    @PostMapping("/check-password")
    public ResponseEntity<Boolean> checkPassword(
            @RequestParam String rawPassword,
            @RequestParam String encodedPassword) {
        // Check if the raw password matches the encoded one
        boolean isMatch = userService.checkPassword(rawPassword, encodedPassword);
        return ResponseEntity.ok(isMatch);
    }

    

    @GetMapping("normal-users")
    public ResponseEntity<List<Map<String, Object>>> getNormalUsersPublicInfo() {
            List<Map<String, Object>> normalUsers = userService.getNormalUsersPublicInfo();
            return ResponseEntity.ok(normalUsers);
        }

    @GetMapping("admin/dashboard")
    public ResponseEntity<String> adminDashboard() {
            return ResponseEntity.ok("Admin Dashboard"); // Simple string response
        }
        
        
    @GetMapping("user/dashboard")
    public ResponseEntity<String> userDashboard() {
                return ResponseEntity.ok("User Dashboard"); // Simple string response
            }

}

