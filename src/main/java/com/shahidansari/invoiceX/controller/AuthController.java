package com.shahidansari.invoiceX.controller;

import com.shahidansari.invoiceX.dto.LoginRequest;
import com.shahidansari.invoiceX.dto.RegisterRequest;
import com.shahidansari.invoiceX.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Email verified successfully"));

    }
}
