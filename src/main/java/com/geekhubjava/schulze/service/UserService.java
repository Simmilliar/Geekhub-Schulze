package com.geekhubjava.schulze.service;

import com.geekhubjava.schulze.model.User;
import com.geekhubjava.schulze.model.form.RegistrationForm;
import com.geekhubjava.schulze.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegistrationForm registrationForm) {
        String passwordHash = passwordEncoder.encode(registrationForm.getPassword());
        User newUser = new User(registrationForm.getLogin(), passwordHash);
        userRepository.createNewUser(newUser);
        return newUser;
    }

    public boolean isLoginAlreadyExists(String login) {
        return userRepository.isLoginAlreadyExists(login);
    }

    public Optional<User> getUserByLogin(String login) {
        return userRepository.getUserByLogin(login);
    }
}
