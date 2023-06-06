package com.example.jwt.reverse.config;

public interface JwtProperties {
    String SECRET = "cos";//우리 서버만 알고 잇는 비밀값
    int EXPIRATION_TIME = 10000*5;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_ACCESS = "accessToken";
    String HEADER_REFRESH = "refreshToken";
}
