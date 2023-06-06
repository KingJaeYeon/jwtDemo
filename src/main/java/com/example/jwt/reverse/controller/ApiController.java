package com.example.jwt.reverse.controller;

import com.example.jwt.reverse.mapper.UserRepository;
import com.example.jwt.reverse.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ApiController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("hello")
    public String hello(){
        return "hello";
    }
    @GetMapping("/loginForm")
    public String loginPage(){
        return "login";
    }
    @GetMapping("/")
    public String index(){
        return "redirect:/user";
    }
    @GetMapping("/join")
    public String joinPage(){
        return "join";
    }
    @PostMapping("/join")
    public String join(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        System.out.println(user);
        userRepository.insertUser(user);
        return "redirect:/loginForm";
    }
//    @PostMapping("/login")
//    public String login(User user){
//        System.out.println(user);
//        return "hello";
//    }
}
