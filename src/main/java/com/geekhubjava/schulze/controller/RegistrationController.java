package com.geekhubjava.schulze.controller;

import com.geekhubjava.schulze.model.User;
import com.geekhubjava.schulze.model.UserAuthDetails;
import com.geekhubjava.schulze.model.form.RegistrationForm;
import com.geekhubjava.schulze.model.response.RegistrationSuccess;
import com.geekhubjava.schulze.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        userService.registerUser(registrationForm);
        return new RegistrationSuccess();
    }

    @GetMapping(value = "/api/users/me", produces = "application/json")
    public String getMyName(Authentication authentication) {
        long userId = ((UserAuthDetails) authentication.getPrincipal()).getId();
        String login = userService.getUserById(userId).map(User::getLogin)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        return "{\"name\":\"" + login + "\"}";
    }
}
