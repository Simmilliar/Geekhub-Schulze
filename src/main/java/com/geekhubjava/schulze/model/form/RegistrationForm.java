package com.geekhubjava.schulze.model.form;

import com.geekhubjava.schulze.model.form.constraint.PasswordsMatch;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PasswordsMatch
public class RegistrationForm {

    @NotNull
    @NotEmpty
    private String login;

    @NotNull
    @NotEmpty
    @Size(min = 8)
    private String password;

    @NotNull
    private String passwordConfirm;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
