package com.assignment.fdp.service;

import com.assignment.fdp.config.JwtService;
import com.assignment.fdp.dto.AuthenticationRequest;
import com.assignment.fdp.dto.AuthenticationResponse;
import com.assignment.fdp.dto.RegisterRequest;
import com.assignment.fdp.model.Role;
import com.assignment.fdp.model.User;
import com.assignment.fdp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
       // If you need a new Admin, you can either change it directly in your PostgreSQL database using
                // SQL UPDATE command or temporarily change Role.VIEWER to Role.ADMIN, run it once
                // and then change it back
                .role(Role.VIEWER)
                .isActive(true)
                .build();

        repository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}