package com.hepl.protocol.responses;

import com.hepl.protocol.interfaces.Response;

import javax.crypto.SecretKey;

public class requestSecureResponse implements Response {

    private SecretKey cleSession;

    public requestSecureResponse(SecretKey cleSession) {
        this.cleSession = cleSession;
    }

    public SecretKey getCleSession() {
        return cleSession;
    }

    public void setCleSession(SecretKey cleSession) {
        this.cleSession = cleSession;
    }
}
