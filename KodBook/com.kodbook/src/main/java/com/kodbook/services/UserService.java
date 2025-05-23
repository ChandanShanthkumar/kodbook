package com.kodbook.services;

import java.util.Optional;

import com.kodbook.entities.User;

@SuppressWarnings("unused")
public interface UserService {

    void addUser(User user);

    boolean userExists(String username, String email);

    boolean validateUser(String username, String password);

    void updateUser(User user);

	Optional<User> getUser(String username);

}
