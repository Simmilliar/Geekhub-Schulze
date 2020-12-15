package com.geekhubjava.schulze.model.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RegistrationForm {

    @NotNull
    @NotEmpty
    private String login;

    @NotNull
    @NotEmpty
    @Size(min = 8)
    private String password;

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
}
