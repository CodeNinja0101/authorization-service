package com.example.jwt.controller;

import com.example.jwt.dto.ApiResponse;
import com.example.jwt.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/user/hello")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> userHello() {
        return ResponseEntity.ok(ResponseUtils.successfulRequestResponse("Hello, User!", null));
    }

    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminHello() {
        return ResponseEntity.ok(ResponseUtils.successfulRequestResponse("Hello, Admin!", null));
    }
}