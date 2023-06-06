package com.example.jwt.reverse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String username;
    private String message;
    private String jwtToken;
    private String refreshToken;
}
