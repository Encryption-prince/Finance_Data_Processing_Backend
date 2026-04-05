package com.assignment.fdp.controller;

import com.assignment.fdp.dto.RegisterRequest;
import com.assignment.fdp.model.Role;
import com.assignment.fdp.model.User;
import com.assignment.fdp.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name="User Controller API")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody RegisterRequest request) {
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();

        return ResponseEntity.ok(userRepository.save(newUser));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!!!"));

        user.setActive(active);

        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role newRole) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!!!!"));

        user.setRole(newRole);

        return ResponseEntity.ok(userRepository.save(user));
    }
}