package com.app.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.app.messaging.domain.NormalUser;
import com.app.messaging.domain.User;
import com.app.messaging.repo.UserRepo;
import com.app.messaging.service.UserService;

import java.util.Optional;

@SpringBootTest // Load full Spring context for integration tests
@Transactional  // Rollback after each test to avoid affecting other tests
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    @Rollback // Ensure changes are rolled back after the test
    void testCreateUserIntegration() {
        // Arrange: Create a new NormalUser
        NormalUser newUser = new NormalUser();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        // Act: Call the service to create the user
        NormalUser createdUser = userService.createUser(newUser);

        // Assert: Verify the user was saved in the database
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());

        Optional<User> foundUser = userRepo.findByUsername("testuser");
        assertTrue(foundUser.isPresent(), "User should be found in the database");
    }

    @Test
    @Rollback
    void testUpdateUserIntegration() {
        // Arrange: Create a user in the repository
        NormalUser user = new NormalUser();
        user.setUsername("oldUsername");
        user.setPassword(passwordEncoder.encode("password"));
        userRepo.save(user);

        // Act: Update the user
        User updatedUser = userService.updateUser(user.getId(), "newUsername");

        // Assert: Verify the user was updated
        assertNotNull(updatedUser);
        assertEquals("newUsername", updatedUser.getUsername());

        Optional<User> foundUser = userRepo.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("newUsername", foundUser.get().getUsername());
    }

    @Test
    @Rollback
    void testDeleteCurrentUserIntegration() {
        // Arrange: Create a new NormalUser and set up the security context
        NormalUser user = new NormalUser();
        user.setUsername("deleteUser");
        user.setPassword(passwordEncoder.encode("password"));
        userRepo.save(user);

        // Mock security context
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("deleteUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act: Delete the current user
        userService.deleteCurrentUser("deleteUser", "password");

        // Assert: Verify the user was deleted
        Optional<User> deletedUser = userRepo.findByUsername("deleteUser");
        assertTrue(deletedUser.isEmpty(), "User should be deleted from the database");
    }
}
