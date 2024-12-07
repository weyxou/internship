package com.example.internship.controllers;

import com.example.internship.config.Jwttokens;
import com.example.internship.dto.ResetPasswordDto;
import com.example.internship.dto.SigninDto;
import com.example.internship.dto.SignupDto;
import com.example.internship.entities.User;
import com.example.internship.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private Jwttokens jwttokens;

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwttokens(Jwttokens jwttokens) {
        this.jwttokens = jwttokens;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto signupDto) {
        if (userRepo.existsUserByUsername(signupDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose another name");
        }
        if (userRepo.existsUserByEmail(signupDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose another email");
        }

        User user = new User();
        user.setUsername(signupDto.getUsername());
        user.setEmail(signupDto.getEmail());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("Successfully registered");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninDto signinDto) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signinDto.getEmail(), signinDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwttokens.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        Optional<User> optionalUser = userRepo.findByEmail(resetPasswordDto.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();

        if (resetPasswordDto.getPassword() == null || resetPasswordDto.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password cannot be empty");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        userRepo.save(user);

        return ResponseEntity.ok("Password successfully reset");
    }

}
