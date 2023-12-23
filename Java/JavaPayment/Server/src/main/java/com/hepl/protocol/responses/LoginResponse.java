package com.hepl.protocol.responses;

import com.hepl.protocol.interfaces.Response;

public class LoginResponse implements Response{
    private boolean success;

    public LoginResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
