package com.hepl.protocol.responses;

import com.hepl.model.Facture;
import com.hepl.protocol.interfaces.Response;

import java.io.Serializable;
import java.util.ArrayList;

public class GetFacturesResponse implements Response{
    private ArrayList<Facture> factures;

    public GetFacturesResponse(ArrayList<Facture> factures) {
        this.factures = factures;
    }

    public ArrayList<Facture> getFactures() {
        return factures;
    }
}
