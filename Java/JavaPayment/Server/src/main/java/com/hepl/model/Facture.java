package com.hepl.model;

import java.util.Date;

public class Facture {
    private int id;
    private Date date;
    private float amount;
    private boolean payed;

    public Facture(int id, Date date, float amount, boolean payed) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.payed = payed;
    }

    public boolean isPayed() {
        return payed;
    }

    public float getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }
}
