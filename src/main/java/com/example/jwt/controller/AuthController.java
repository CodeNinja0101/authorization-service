package com.example.jwt.controller;

import com.example.jwt.config.JwtUtil;
import com.example.jwt.dto.ApiResponse;
import com.example.jwt.dto.AuthRequest;
import com.example.jwt.entity.RefreshToken;
import com.example.jwt.entity.User;
import com.example.jwt.exception.UserNotFoundException;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.service.AuthService;
import com.example.jwt.service.RefreshTokenService;
import com.example.jwt.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService,
                          UserRepository userRepository, JwtUtil jwtUtil) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil; // Inject JwtUtil
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody AuthRequest authRequest) {
        log.info("Received register request for username: {}", authRequest.getUsername());
        authService.register(authRequest);
        log.info("User registered successfully: {}", authRequest.getUsername());
        return ResponseEntity.ok(ResponseUtils.successfulRequestResponse("User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody AuthRequest authRequest) {
        log.info("Login attempt for username: {}", authRequest.getUsername());
        String token = String.valueOf(authService.login(authRequest));
        log.info("Login successful for username: {}", authRequest.getUsername());
        return ResponseEntity.ok(ResponseUtils.successfulRequestResponse("Login successful", token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> request) {
        log.info("Refresh token request received");
        String refreshToken = request.get("refreshToken");
        RefreshToken token = refreshTokenService.verifyRefreshToken(refreshToken);
        User user = userRepository.findByUsername(token.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found for refresh token");
                    return new UserNotFoundException("User not found");
                });
        Set<String> roles = user.getRoles() != null ? user.getRoles() : new HashSet<>();
        if (roles.isEmpty()) {
            roles.add("ROLE_USER");
        }
        String newAccessToken = jwtUtil.generateToken(user.getUsername(), roles);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", refreshToken);
        log.info("Token refreshed for user '{}'", user.getUsername());
        return ResponseEntity.ok(ResponseUtils.successfulRequestResponse("Token refreshed", tokens));
    }
}