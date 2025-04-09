package com.example.authService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authService.entity.User;
import com.example.authService.enums.Role;
import com.example.authService.exception.type_exception.BadRequestException;
import com.example.authService.exception.type_exception.ConflictException;
import com.example.authService.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initializeAdmin() {
        String adminUsername = "admin";
        String adminPassword = "admin";

        Optional<User> existingAdmin = userRepository.findByUsername(adminUsername);
        if (existingAdmin.isEmpty()) {
            User admin = new User(adminUsername, passwordEncoder.encode(adminPassword),
                    List.of(Role.ADMIN, Role.USER));
            userRepository.save(admin);
            System.out.println("Admin account created: " + adminUsername);
        } else {
            System.out.println("admin account is created!");
            System.out.println("admin account is created!");
            System.out.println("admin account is created!");
        }
    }

    // create user
    public User creatUser(String username, String password) {

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username is already taken");
        }

        User newUser = createNewUser(username, password);

        try {
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user: " + e.getMessage());
        }
    }

    // get all user
    public Page<User> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    // find user by username
    public User findUserByUserName(String username) {
        try {
            User resultUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("username is not found"));
            return resultUser;
        } catch (Exception e) {
            System.out.println("Error in find user by username");
            System.err.println(e.getMessage());
            throw new RuntimeException("Internal Server Error");
        }
    }

    // create new user =================== UTIL =================
    private User createNewUser(String username, String password) throws BadRequestException {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Username must not be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("Password must not be empty");
        }

        Role userRole = Role.USER;

        return new User(username, passwordEncoder.encode(password), List.of(userRole));
    }

}
