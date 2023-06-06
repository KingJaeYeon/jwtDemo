package com.example.jwt.reverse.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.reverse.config.JwtProperties;
import com.example.jwt.reverse.config.auth.PrincipalDetails;
import com.example.jwt.reverse.mapper.UserRepository;
import com.example.jwt.reverse.model.AuthResponseDTO;
import com.example.jwt.reverse.model.Token;
import com.example.jwt.reverse.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Random;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private ObjectMapper om = new ObjectMapper();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("doFilter");
        super.doFilter(request, response, chain);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter :  로그인 시도중");
        try {
            //만약 로그인창이 아닌 토큰유효기간 만료가 되지않아 자동으로 들어온 유저일 경우 user가 null이므로 조치를 해줘야함
            User user = om.readValue(request.getInputStream(), User.class);
            if (!isValid(user)) {
                AuthResponseDTO authResponse = new AuthResponseDTO();
                if (isNotFoundUser(user))
                    authResponse.setMessage("NotFoundUsername");
                else if (isNotBCryptPasswordMatches(user))
                    authResponse.setMessage("Password wrong");
                om = new ObjectMapper();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(om.writeValueAsString(authResponse));
                return null;
            }
            System.out.println("로그인 유효성 검사 패스");
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);  // UserDetailsService 실행됨


            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료됨: " + principalDetails.getUser().getUsername()); // 로그인이 되었다는 뜻
            System.out.println("authentication: " + authentication);
            return authentication;
        } catch (Exception e) {
            System.out.println("JwtAuthenticationFilter : 에러 발생");
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication");
        //경우의 수를 줘야함
        //유저가 처음 들어왔을 때와 기존 유저가 토큰 만료시간이 지나서 들어왔을 때(refresh토큰 확인해야함)
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        String jwtToken = JWT.create()
                .withSubject("cosToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        String refreshToken = createRandomCode();
        Token token = Token.builder()
                .username(principalDetails.getUser().getUsername())
                .accessToken(jwtToken)
                .refreshToken(refreshToken).build();
        userRepository.insertToken(token);

        response.addHeader(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + jwtToken);
        response.addHeader(JwtProperties.HEADER_REFRESH, refreshToken);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("unsuccessfulAuthentication");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean isValid(User user) {
        if (isNotFoundUser(user))
            return false;
        return !isNotBCryptPasswordMatches(user);
    }

    private boolean isNotFoundUser(User user) {
        return userRepository.findUsername(user.getUsername()).isEmpty();
    }

    private boolean isNotBCryptPasswordMatches(User user) {
        String savedPassword = userRepository.findUsername(user.getUsername()).get().getPassword();
        return !bCryptPasswordEncoder.matches(user.getPassword(), savedPassword);
    }

    private String createRandomCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char)((int)random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int)random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }
}
