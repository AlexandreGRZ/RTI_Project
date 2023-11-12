package com.hepl.protocol.requests;

import com.hepl.protocol.interfaces.Request;

public class PayFactureRequest implements Request {
    private int idFacture;
    private String numCard;

    public PayFactureRequest(int idFacture, String numCard) {
        this.idFacture = idFacture;
        this.numCard = numCard;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public String getNumCard() {
        return numCard;
    }
}
