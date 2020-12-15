package com.geekhubjava.schulze.controller;

import com.geekhubjava.schulze.exception.ConflictException;
import com.geekhubjava.schulze.model.form.RegistrationForm;
import com.geekhubjava.schulze.model.response.RegistrationSuccess;
import com.geekhubjava.schulze.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/registration")
    public RegistrationSuccess registerUser(@RequestBody @Valid RegistrationForm registrationForm) {
        if (userService.isLoginAlreadyExists(registrationForm.getLogin())) {
            throw new ConflictException("User already exists");
        }
        userService.registerUser(registrationForm);
        return new RegistrationSuccess();
    }
}
