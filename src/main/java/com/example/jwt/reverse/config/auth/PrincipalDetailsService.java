package com.example.jwt.reverse.config.auth;

import com.example.jwt.reverse.mapper.UserRepository;
import com.example.jwt.reverse.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PrincipalDetailsService: loadUserByUsername() 실행");
        User user = userRepository.findUsername(username).orElse(null);
        System.out.println(user);
        return new PrincipalDetails(user);
    }
}
