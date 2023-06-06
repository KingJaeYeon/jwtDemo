package com.example.jwt.reverse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

    @GetMapping("hello2")
    public String hello2(){
        return "hello2";
    }

    @GetMapping("/user")
    public String user(){
        return "user";
    }
}
