package com.example.jwt.reverse.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.reverse.config.JwtProperties;
import com.example.jwt.reverse.config.auth.PrincipalDetails;
import com.example.jwt.reverse.mapper.UserRepository;
import com.example.jwt.reverse.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("JwtAuthorizationFilter: JwtAuthorizationFilter() / 인증이나 권한이 필요한 주소 요청");

        String jwtHeader = request.getHeader(JwtProperties.HEADER_ACCESS);
        System.out.println("jwtHeader :" + jwtHeader);

        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            System.out.println("정상적인 사용자가 아닙니다.");
            chain.doFilter(request, response);
        } else {
            String jwtToken = request.getHeader(JwtProperties.HEADER_ACCESS).replace(JwtProperties.TOKEN_PREFIX, "");
            System.out.println("jwtToken: " + jwtToken);
            String username =
                    JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken).getClaim("username").asString();
            System.out.println("2");
            if (username != null) {
                User user = userRepository.findUsername(username).get();

                PrincipalDetails principalDetails = new PrincipalDetails(user);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            } else {
                chain.doFilter(request, response);
            }
        }
    }
}
