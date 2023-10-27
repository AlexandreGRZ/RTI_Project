package com.hepl.clientachat.Model;

import java.util.ArrayList;

public class Model {
    private ArrayList<Article> cart;
    private Article currentArticle;
    private static Model instance;

    // Singleton instance getter----------------------------------------------------------------------------------------
    private Model() {

    }

    public static Model getInstance() {
        if (instance == null)
            instance = new Model();
        return instance;
    }

    // Getters and Setters----------------------------------------------------------------------------------------------
    public Article getCurrentArticle() {
        return currentArticle;
    }

    public void setCurrentArticle(Article article){
        currentArticle = article;
    }

    public Article getArticleInCartAtIndex(int index) {
        return cart.get(index);
    }

    public void setCart(ArrayList<Article> newCart) {
        cart = newCart;
    }

    public ArrayList<Article> getCart(){
        return cart;
    }
}
