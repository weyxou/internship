package com.example.internship.conrollers;

import com.example.internship.entities.User;
import com.example.internship.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepo.save(user);
    }
}
