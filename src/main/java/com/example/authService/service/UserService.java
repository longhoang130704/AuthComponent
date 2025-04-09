package com.example.authService.service;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authService.entity.User;
import com.example.authService.enums.Role;
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
            throw new RuntimeException("username is existed");
        }

        try {
            User newUser = createNewUser(username, password);

            User savedUser = userRepository.save(newUser);

            return savedUser;

        } catch (Exception e) {
            System.out.println("Erorr in create user in UserUtil or save user to database");
            System.err.println(e.getMessage());
            throw new RuntimeException("Internal Server Error");
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
        if (username == null || username.isEmpty())
            throw new BadRequestException("username is not exist");
        if (password == null || password.isEmpty())
            throw new BadRequestException("password is not exist");

        // Tất cả đều là user chỉ có 1 admin duy nhất
        Role userRole = Role.USER;

        return new User(username, passwordEncoder.encode(password), List.of(userRole));
    }
}
