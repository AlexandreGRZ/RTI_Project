package com.hepl.protocol.requests;

import com.hepl.protocol.interfaces.Request;

public class PayFactureRequest implements Request {
    private int idFacture;
    private long numCard;

    public PayFactureRequest(int idFacture, long numCard) {
        this.idFacture = idFacture;
        this.numCard = numCard;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public long getNumCard() {
        return numCard;
    }
}
