package com.app.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.messaging.domain.NormalUser;
import com.app.messaging.domain.User;
import com.app.messaging.repo.UserRepo;
import com.app.messaging.service.UserService;

import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        NormalUser newUser = new NormalUser();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        NormalUser savedUser = new NormalUser();
        savedUser.setId(1);
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedpassword");

        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(userRepo.save(any(NormalUser.class))).thenReturn(savedUser);

        NormalUser result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(passwordEncoder).encode("password");
        verify(userRepo).save(any(NormalUser.class));
    }

    @Test
    void testUpdateUser() {
        NormalUser existingUser = new NormalUser(); // Use concrete class
        existingUser.setId(1);
        existingUser.setUsername("oldUsername");

        NormalUser updatedUser = new NormalUser(); // Use concrete class
        updatedUser.setId(1);
        updatedUser.setUsername("newUsername");

        when(userRepo.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(NormalUser.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1, "newUsername");

        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        verify(userRepo).findById(1);
        verify(userRepo).save(any(NormalUser.class));
    }


    @Test
    void testGetCurrentAuthenticatedUser() {
        User currentUser = new NormalUser();
        currentUser.setUsername("currentUser");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("currentUser");
        when(userRepo.findByUsername("currentUser")).thenReturn(Optional.of(currentUser));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User result = userService.getCurrentAuthenticatedUser();

        assertNotNull(result);
        assertEquals("currentUser", result.getUsername());
        verify(userRepo).findByUsername("currentUser");
    }

    @Test
void testDeleteCurrentUser() {
    // Arrange
    User currentUser = new NormalUser();
    currentUser.setUsername("currentUser");
    currentUser.setPassword("encodedpassword");

    Authentication authentication = mock(Authentication.class);
    UserDetails userDetails = mock(UserDetails.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("currentUser");
    when(passwordEncoder.matches("password", "encodedpassword")).thenReturn(true);
    when(userRepo.findByUsername("currentUser")).thenReturn(Optional.of(currentUser));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Act
    userService.deleteCurrentUser("currentUser", "password");

    // Assert
    verify(userRepo).delete(currentUser);

    // Simulate that the user is no longer available in the repository
    when(userRepo.findByUsername("currentUser")).thenReturn(Optional.empty());

    // Check that the user is no longer found
    Optional<User> deletedUser = userRepo.findByUsername("currentUser");
    assertTrue(deletedUser.isEmpty(), "User should not be found after deletion");
}

}
