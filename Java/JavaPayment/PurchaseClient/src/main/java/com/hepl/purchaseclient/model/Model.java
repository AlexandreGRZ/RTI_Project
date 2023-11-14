package com.hepl.purchaseclient.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Model {
    private ArrayList<Article> cart;
    private Article currentArticle;
    private ObservableList<Article> observableListOfArticles;
    private static Model instance;

    // Singleton instance getter----------------------------------------------------------------------------------------
    private Model() {
        cart = new ArrayList<>();
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

    public ObservableList<Article> getObservableListOfArticles() {
        return observableListOfArticles;
    }

    public void setObservableListOfArticles(ObservableList<Article> observableListOfArticles) {
        this.observableListOfArticles = observableListOfArticles;
    }

    public void TransformArrayListToObservableList(){
        setObservableListOfArticles(FXCollections.observableArrayList());
        for (Article a : getCart())
        {
            getObservableListOfArticles().add(a);
        }
    }
}
