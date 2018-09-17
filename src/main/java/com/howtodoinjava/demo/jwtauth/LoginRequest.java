package com.howtodoinjava.demo.jwtauth;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

public class LoginRequest implements Serializable{

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
