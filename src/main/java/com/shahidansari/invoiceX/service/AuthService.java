package com.shahidansari.invoiceX.service;

import com.shahidansari.invoiceX.dto.AuthResponse;
import com.shahidansari.invoiceX.dto.LoginRequest;
import com.shahidansari.invoiceX.dto.RegisterRequest;
import com.shahidansari.invoiceX.entity.Role;
import com.shahidansari.invoiceX.entity.User;
import com.shahidansari.invoiceX.repository.UserRepository;
import com.shahidansari.invoiceX.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())) throw new RuntimeException("Email already registered !");

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .emailVerified(false)
                .build();

        userRepository.save(newUser);

        // TO DO: send verification email

        return objectMapper.convertValue(newUser,AuthResponse.class);

    }

    public AuthResponse login(LoginRequest request){

        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("Invalid email"));

        if(!passwordEncoder.matches(request.getPassword(),existingUser.getPassword())){
            throw new UsernameNotFoundException("Invalid password");
        }

//        if(!existingUser.isEmailVerified()){
//            throw new RuntimeException("Please verify your email before logging in.");
//        }


        String token = jwtUtil.generateToken(existingUser.getId());

        AuthResponse response = AuthResponse.builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .emailVerified(existingUser.isEmailVerified())
                .role(existingUser.getRole().name())
                .createdAt(existingUser.getCreatedAt())
                .lastUpdatedAt(existingUser.getLastUpdatedAt())
                .token(token)
                .build();

        return response;
    }

}
