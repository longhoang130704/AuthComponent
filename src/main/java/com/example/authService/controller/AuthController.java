package com.example.authService.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authService.dto.request.Auth.AuthRequest;
import com.example.authService.entity.User;
import com.example.authService.service.AuthService;
import com.example.authService.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping
    public String available() {
        return "Server is still running";
    }

    // login and receive accesstoken(1 hour), refreshtoken(7 day)
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest authRequest) {
        try {
            return authService.login(authRequest.getUsername(), authRequest.getPassword());
        } catch (Exception e) {
            System.out.println("Error in login controller");
            System.err.println(e.getMessage());
            return Map.of("message", "Internal Server Error");
        }
    }

    // register
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody AuthRequest authRequest) {

        User registerUser = userService.creatUser(authRequest.getUsername(), authRequest.getPassword());
        return Map.of("userId", registerUser.getId(),
                "username", registerUser.getUsername());

    }
}
