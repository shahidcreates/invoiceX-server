package com.shahidansari.invoiceX.service;

import com.shahidansari.invoiceX.dto.RegisterRequest;
import com.shahidansari.invoiceX.entity.User;
import com.shahidansari.invoiceX.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())) throw new RuntimeException("Email already registered !");

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);

        return "User registered successfully.";

    }
}
