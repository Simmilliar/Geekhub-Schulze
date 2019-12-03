package com.geekhubjava.schulze.model.form.constraint;

import com.geekhubjava.schulze.model.form.RegistrationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        RegistrationForm registrationForm = (RegistrationForm) obj;
        return registrationForm.getPassword().equals(registrationForm.getPasswordConfirm());
    }
}
