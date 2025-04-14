package com.example.jwt.service;

import com.example.jwt.config.JwtUtil;
import com.example.jwt.dto.AuthRequest;
import com.example.jwt.entity.User;
import com.example.jwt.exception.InvalidCredentialsException;
import com.example.jwt.exception.UserNotFoundException;
import com.example.jwt.exception.UsernameAlreadyExistsException;
import com.example.jwt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(AuthRequest authRequest) {
        log.info("Checking if username '{}' already exists", authRequest.getUsername());
        Boolean exists = userRepository.existsByUsername(authRequest.getUsername());
        if (exists != null && exists) {
            log.warn("Attempt to register existing username: {}", authRequest.getUsername());
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.getRoles().add("ROLE_USER");
        userRepository.save(user);
        log.info("User '{}' registered successfully", authRequest.getUsername());
    }

    public String login(AuthRequest authRequest) {
        log.info("Login attempt for username: {}", authRequest.getUsername());
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", authRequest.getUsername());
                    return new UserNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for username: {}", authRequest.getUsername());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        Set<String> roles = user.getRoles() != null ? user.getRoles() : new HashSet<>();
        if (roles.isEmpty()) {
            roles.add("ROLE_USER");
        }

        String token = jwtUtil.generateToken(user.getUsername(), roles);
        log.info("Token generated for user '{}'", user.getUsername());
        return token;
    }
}