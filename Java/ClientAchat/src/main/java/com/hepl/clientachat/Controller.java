package com.hepl.clientachat;

import com.hepl.clientachat.Model.Article;
import com.hepl.clientachat.Model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class Controller {
    @FXML
    private TextField TextFieldLogin;
    @FXML
    private TextField TextFieldPassword;
    @FXML
    private CheckBox checkBoxNewUser;
    @FXML
    private Spinner SpinnerQuantity;
    @FXML
    private ImageView imageArticle;
    @FXML
    private TextField cartPrice;
    @FXML
    private TableView TableViewPanier;
    @FXML
    private TableColumn<Article, String> ColumnArticle;
    @FXML
    private TableColumn<Article, String> ColumnPrice;
    @FXML
    private TableColumn<Article, String> ColumnQuantity;

    private Model model;

    public void initialize(){
        model = Model.getInstance();
    }

    // Button click handlers--------------------------------------------------------------------------------------------
    @FXML
    protected void onLoginClick() {

    }
    @FXML
    protected void onLogoutClick() {

    }
    @FXML
    protected void onNextClick() {

    }
    @FXML
    protected void onPreviousClick() {

    }
    @FXML
    protected void onBuyClick() {

    }
    @FXML
    protected void onDeleteArticleClick(){

    }
    @FXML
    protected void onEmptyCartClick(){

    }
    @FXML
    protected void onConfirmClick(){

    }

    // Update cart tableView with article in cart from model
    private void setCartTable(){
        TableViewPanier.getItems().setAll(model.getCart());

        ColumnArticle.setCellValueFactory(c -> c.getValue().articleProperty());
        ColumnPrice.setCellValueFactory(c -> c.getValue().priceProperty());
        ColumnQuantity.setCellValueFactory(c -> c.getValue().quantityProperty());
    }
}