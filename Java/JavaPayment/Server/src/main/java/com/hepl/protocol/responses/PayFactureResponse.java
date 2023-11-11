package com.hepl.protocol.responses;

import com.hepl.protocol.interfaces.Response;

public class PayFactureResponse implements Response {
    private boolean success;

    public PayFactureResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
