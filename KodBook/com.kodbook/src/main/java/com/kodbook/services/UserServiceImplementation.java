package com.kodbook.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kodbook.entities.User;
import com.kodbook.repositories.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository repo;

    @Override
    public void addUser(User user) {
        repo.save(user);
    }

    @Override
    public boolean userExists(String username, String email) {
        return repo.existsByUsername(username) || repo.existsByEmail(email);
    }

    @Override
    public boolean validateUser(String username, String password) {
        Optional<User> userOpt = repo.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // In production, use BCryptPasswordEncoder to match hashed passwords!
            return password.equals(user.getPassword());
        }
        return false;
    }

    @Override
    public Optional<User> getUser(String username) {
        return repo.findByUsername(username);
    }

    @Override
    public void updateUser(User user) {
        repo.save(user);
    }
}
