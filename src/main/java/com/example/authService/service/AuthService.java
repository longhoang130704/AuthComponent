package com.example.authService.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authService.entity.User;
import com.example.authService.repository.UserRepository;

@Service
public class AuthService {
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JWTService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // login to get accesstoken and refreshtoken
    public Map<String, String> login(String username, String password) {
        try {
            User user = checkInputLogin(username, password);

            List<String> roles = user.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());

            String generateAccesstokeString = jwtService.generateAccessToken(user.getUsername(), roles);
            String generateRefreshString = jwtService.generateRefreshToken(user.getUsername());

            return Map.of("accesstoken", generateAccesstokeString, "refreshtoken", generateRefreshString);
        } catch (Exception e) {
            System.out.println("Error in login");
            System.err.println(e.getMessage());
            throw new RuntimeException("Internal Server Error");
        }
    }

    // util
    private User checkInputLogin(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}
