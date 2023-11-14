package com.hepl.protocol.requests;

import com.hepl.protocol.interfaces.Request;

public class LoginRequest implements Request {
    String login;
    String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
