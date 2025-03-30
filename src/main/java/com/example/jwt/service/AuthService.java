package com.example.jwt.service;

import com.example.jwt.config.JwtUtil;
import com.example.jwt.dto.AuthRequest;
import com.example.jwt.entity.User;
import com.example.jwt.exception.InvalidCredentialsException;
import com.example.jwt.exception.UserNotFoundException;
import com.example.jwt.exception.UsernameAlreadyExistsException;
import com.example.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(AuthRequest authRequest) {
        Boolean exists = userRepository.existsByUsername(authRequest.getUsername());
        if (exists != null && exists) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        userRepository.save(user);
    }

    public String login(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        return jwtUtil.generateToken(user.getUsername());
    }
}