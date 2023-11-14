package com.hepl.protocol.requests;

import com.hepl.protocol.interfaces.Request;

public class GetFacturesRequest implements Request {
    private int idClient;

    public GetFacturesRequest(int idClient) {
        this.idClient = idClient;
    }

    public int getIdClient() {
        return idClient;
    }
}
