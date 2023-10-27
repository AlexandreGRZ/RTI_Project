package com.hepl.clientachat.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Article {
    private int id;
    private String name;
    private float price;
    private int stock;
    private int quantity;
    private String image;

    // Constructors-----------------------------------------------------------------------------------------------------
    public Article(int id, String name, float price, int stock, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.image = image;
    }

    public Article(int id, String name, int quantity, float price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters----------------------------------------------------------------------------------------------------------
    public String getImage() {
        return image;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStock() {
        return stock;
    }

    public float getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public StringProperty articleProperty(){
        return new SimpleStringProperty(name);
    }

    public StringProperty priceProperty(){
        return new SimpleStringProperty(Float.toString(price));
    }

    public StringProperty quantityProperty(){
        return new SimpleStringProperty(Integer.toString(quantity));
    }
}
