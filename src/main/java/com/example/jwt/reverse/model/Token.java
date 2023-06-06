package com.example.jwt.reverse.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private String username;
    private String accessToken;
    private String refreshToken;
    private Date create_Date;

    @Builder
    public Token(String username,String accessToken,String refreshToken){
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
