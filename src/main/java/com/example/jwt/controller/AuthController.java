package com.example.jwt.controller;

import com.example.jwt.dto.ApiResponse;
import com.example.jwt.dto.AuthRequest;
import com.example.jwt.service.AuthService;
import com.example.jwt.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody AuthRequest authRequest) {
        authService.register(authRequest);
        return ResponseEntity.ok(ResponseUtils.successfulRequestResponse("User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody AuthRequest authRequest) {
        String token = authService.login(authRequest);
        return ResponseEntity.ok(ResponseUtils.successfulRequestResponse("Login successful", token));
    }
}