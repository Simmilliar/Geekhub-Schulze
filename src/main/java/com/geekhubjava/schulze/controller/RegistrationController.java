package com.geekhubjava.schulze.controller;

import com.geekhubjava.schulze.model.form.RegistrationForm;
import com.geekhubjava.schulze.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        RegistrationForm registrationForm = new RegistrationForm();
        model.addAttribute("user", registrationForm);
        return "registration";
    }

    @PostMapping("/registration")
    public ModelAndView registerUser(@ModelAttribute("user") @Valid RegistrationForm registrationForm,
                                     BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            if (userService.isLoginAlreadyExists(registrationForm.getLogin())) {
                bindingResult.rejectValue("login", "login.alreadyExists");
            }
        }
        if (bindingResult.hasErrors()) {
            return new ModelAndView("registration", bindingResult.getModel());
        } else {
            userService.registerUser(registrationForm);
            return new ModelAndView("redirect:/");
        }
    }
}
