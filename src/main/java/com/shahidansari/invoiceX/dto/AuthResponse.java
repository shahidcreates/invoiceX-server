package com.shahidansari.invoiceX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AuthResponse {
    long id;
    private String name;
    private String email;
    private String profileImageUrl;
    private boolean emailVerified;
    private String token;

    private String role;

    private Instant createdAt;

    private Instant lastUpdatedAt;
}
