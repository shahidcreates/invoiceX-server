package com.shahidansari.invoiceX.service;

import com.shahidansari.invoiceX.dto.AuthResponse;
import com.shahidansari.invoiceX.dto.LoginRequest;
import com.shahidansari.invoiceX.dto.RegisterRequest;
import com.shahidansari.invoiceX.entity.Role;
import com.shahidansari.invoiceX.entity.User;
import com.shahidansari.invoiceX.repository.UserRepository;
import com.shahidansari.invoiceX.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Value("${app.base.url}")
    private String appBaseUrl;



    public AuthResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())) throw new RuntimeException("Email already registered !");

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(newUser);
        sendVerificationEmail(newUser);

        return objectMapper.convertValue(newUser,AuthResponse.class);

    }

    private void sendVerificationEmail(User newUser) {
        log.info("Inside AuthService - sendVerificationEmail(): {}",newUser);
        try{

            String link= appBaseUrl+"/api/auth/verify-email?token="+newUser.getVerificationToken();
            String html = "<div style='font-family:sans-serif'>"+
                    "<h2>Verify your email</h2>" +
                    "<p>Hi "+newUser.getName()+" ,please conform your email to activate your account. </p>"+
                    "<p><a href='"+link+
                    "' style='display:inline-block;padding:10px 16px;background:#6366F1;color:#fff;border-radius:6px;text-decoration:none'>Verify Email</a></p>"+
                    "<p>Or copy this link: "+link+" </p>"+
                    "<p>This link expires in 24 hours.</p>"+
                    "</div>";
            emailService.sendHtmlEmail(newUser.getEmail(), "Verify your email",html);
        } catch (Exception e) {
            log.error("Exception occurred at sendVerificationEmail(): {}",e.getMessage());
            throw new RuntimeException("Failed to send verification email : "+e.getMessage());
        }
    }

    public AuthResponse login(LoginRequest request){

        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("Invalid email"));

        if(!passwordEncoder.matches(request.getPassword(),existingUser.getPassword())){
            throw new UsernameNotFoundException("Invalid password");
        }

        if(!existingUser.isEmailVerified()){
            throw new RuntimeException("Please verify your email before logging in.");
        }


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

    public void verifyEmail(String token){
        log.info("Inside AuthService: verifyEmail(): {}",token);
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification token "));
        if(user.getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Verification token expired. Please request new one.");
        }
        user.setEmailVerified(true);
        user.setVerificationExpires(null);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

}
