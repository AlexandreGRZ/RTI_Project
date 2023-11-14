package com.hepl.purchaseclient;

import com.hepl.purchaseclient.model.*;
import com.hepl.purchaseclient.socketlibrary.ClientSocket;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    @FXML
    public Button btnLogin;
    @FXML
    public Button btnLogout;
    @FXML
    public Button btnPreviousElement;
    @FXML
    public Button btnNextElement;
    @FXML
    public Button btnBuy;
    @FXML
    public Button btnDeleteArticles;
    @FXML
    public Button btnEmptyCaddie;
    @FXML
    public Button btnConfirmBuy;
    @FXML
    public TextField TextFieldArticle;
    @FXML
    public TextField TextFieldPrice;
    @FXML
    public TextField TextFieldStock;
    @FXML
    private TextField TextFieldLogin;
    @FXML
    private TextField TextFieldPassword;
    @FXML
    private CheckBox CheckBoxNewUser;
    @FXML
    private Spinner SpinnerQuantity;
    @FXML
    private ImageView ImageViewArticle;
    @FXML
    private TextField TextFieldCartTotalPrice;
    @FXML
    private TableView TableViewPanier;
    @FXML
    private TableColumn<Article, String> ColumnArticle;
    @FXML
    private TableColumn<Article, String> ColumnPrice;
    @FXML
    private TableColumn<Article, String> ColumnQuantity;

    private Model model;

    private static ClientSocket clientSocket;

    private int idElement;


    public void initialize() throws IOException {

        model = Model.getInstance();

        clientSocket = new ClientSocket("127.0.0.1", 50000);
        setIdElement(1);

    }

    // Button click handlers--------------------------------------------------------------------------------------------
    @FXML
    protected void onLoginClick() throws IOException {

        String data = "LOGIN#" + TextFieldLogin.getText() + "#" + TextFieldPassword.getText() + "#" + (CheckBoxNewUser.isSelected() ? 1 : 0);
        System.out.println(data);
        clientSocket.send(data);

        String reponse = clientSocket.receive();

        String[] parts = reponse.split("#");

        if (parts[1].equals("ALREADY") ||  parts[1].equals("ERR")) {
            System.out.println("Il y a un problème !");
        }
        else{
            System.out.println("Vous êtes connecté !");
            LoginChangeBtnDesible();
        }
    }
    @FXML
    protected void onLogoutClick() throws IOException {
        String data = "LOGOUT";
        clientSocket.send(data);

        String reponse = clientSocket.receive();
        String[] parts = reponse.split("#");
        System.out.println(parts[1]);
        if(parts[1].equals("OK")){
            System.out.println("Logout éffectuer !");
            LogoutChangeBtnDesible();
        }

    }
    @FXML
    protected void onNextClick() throws IOException {
        setIdElement(getIdElement() + 1);
        String data = "CONSULT#" + getIdElement();
        clientSocket.send(data);

        String reponse = clientSocket.receive();
        String[] parts = reponse.split("#");
        if(parts[1].equals("ERR")){
            System.out.println("Consult ERR !");
        } else {
            TextFieldArticle.setText(parts[3]);
            TextFieldPrice.setText(parts[4]);
            TextFieldStock.setText(parts[5]);
            ImageViewArticle.setImage(new Image(getClass().getResource("/images/" + parts[6]).toExternalForm()));

        }
    }
    @FXML
    protected void onPreviousClick() throws IOException {
        setIdElement(getIdElement() - 1);
        String data = "CONSULT#" + getIdElement();
        clientSocket.send(data);

        String reponse = clientSocket.receive();
        String[] parts = reponse.split("#");
        if(parts[1].equals("ERR")){
            System.out.println("Consult ERR !");
        } else {
            TextFieldArticle.setText(parts[3]);
            TextFieldPrice.setText(parts[4]);
            TextFieldStock.setText(parts[5]);
            ImageViewArticle.setImage(new Image(getClass().getResource("/images/" + parts[6]).toExternalForm()));

        }
    }
    @FXML
    protected void onBuyClick() throws IOException {

        String data = "ACHAT#" + getIdElement() + "#" + SpinnerQuantity.getValue();
        clientSocket.send(data);

        String reponse = clientSocket.receive();
        String[] parts = reponse.split("#");
        if(parts[1].equals("-1")){
            System.out.println("ACHAT impossible car élément introuvable !");
            System.out.println(parts[5]);
        } else if(parts[4].equals("0")) {
            System.out.println("ACHAT impossible car pas assez de stock !");
        }   else {
            String data2 = "CADDIE";
            clientSocket.send(data2);

            String reponse2 = clientSocket.receive();

            String[] parts2 = reponse2.split("#");

            int i = parts2.length;
            i = (i - 1) / 4;
            System.out.println(i);
            model.setCart(new ArrayList<Article>());
            for (int j = 1, k = 0; j <= i && (1 + k) < parts2.length; j++, k += 4) {
                Article article = new Article(Integer.parseInt(parts2[1 + k]), parts2[2 + k], Integer.parseInt(parts2[4 + k]), Float.parseFloat(parts2[3 + k]));
                model.getCart().add(article);
            }

            model.TransformArrayListToObservableList();

            ColumnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            ColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            ColumnArticle.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableViewPanier.setItems(model.getObservableListOfArticles());

            String data3 = "CONSULT#" + getIdElement();
            clientSocket.send(data3);

            String reponse3 = clientSocket.receive();
            String[] parts3 = reponse3.split("#");
            if(parts3[1].equals("ERR")){
                System.out.println("Consult ERR !");
            } else {
                TextFieldArticle.setText(parts3[3]);
                TextFieldPrice.setText(parts3[4]);
                TextFieldStock.setText(parts3[5]);
                ImageViewArticle.setImage(new Image(getClass().getResource("/images/" + parts3[6]).toExternalForm()));

            }

        }
    }
    @FXML
    protected void onDeleteArticleClick() throws IOException {
        Article ArticleSelectionned = (Article) TableViewPanier.getSelectionModel().getSelectedItem();
        String data = "CANCEL#" + ArticleSelectionned.getId();

        clientSocket.send(data);

        String reponse = clientSocket.receive();

        String[] parts = reponse.split("#");

        if(parts[1].equals("OK")){

            model.getCart().remove(ArticleSelectionned);
            model.TransformArrayListToObservableList();
            TableViewPanier.setItems(model.getObservableListOfArticles());

            String data3 = "CONSULT#" + getIdElement();
            clientSocket.send(data3);

            String reponse3 = clientSocket.receive();
            String[] parts3 = reponse3.split("#");
            if(parts3[1].equals("ERR")){
                System.out.println("Consult ERR !");
            } else {
                TextFieldArticle.setText(parts3[3]);
                TextFieldPrice.setText(parts3[4]);
                TextFieldStock.setText(parts3[5]);
                ImageViewArticle.setImage(new Image(getClass().getResource("/images/" + parts3[6]).toExternalForm()));

            }

        }   else {
            System.out.println("Problème dans le suppression");
        }


    }
    @FXML
    protected void onEmptyCartClick() throws IOException {
        String data = "CANCELALL";

        clientSocket.send(data);

        String reponse = clientSocket.receive();

        String[] parts = reponse.split("#");

        if(parts[1].equals("OK")){
            model.setCart(new ArrayList<>());
            model.TransformArrayListToObservableList();
            TableViewPanier.setItems(model.getObservableListOfArticles());

            String data3 = "CONSULT#" + getIdElement();
            clientSocket.send(data3);

            String reponse3 = clientSocket.receive();
            String[] parts3 = reponse3.split("#");
            if(parts3[1].equals("ERR")){
                System.out.println("Consult ERR !");
            } else {
                TextFieldArticle.setText(parts3[3]);
                TextFieldPrice.setText(parts3[4]);
                TextFieldStock.setText(parts3[5]);
                ImageViewArticle.setImage(new Image(getClass().getResource("/images/" + parts3[6]).toExternalForm()));

            }
        }   else {
            System.out.println("Erreur dans le CancelAll");
        }
    }
    @FXML
    protected void onConfirmClick() throws IOException {
        String data = "CONFIRM";
        clientSocket.send(data);

        String reponse = clientSocket.receive();
        String[] parts3 = reponse.split("#");
        if(parts3[1].equals("ERR")){
            System.out.println("Consult ERR !");
        } else {
            System.out.println("Achat bien éfectuer !");
        }
    }

    // Update cart tableView with article in cart from model
    private void setCartTable(){
        TableViewPanier.getItems().setAll(model.getCart());

        ColumnArticle.setCellValueFactory(c -> c.getValue().articleProperty());
        ColumnPrice.setCellValueFactory(c -> c.getValue().priceProperty());
        ColumnQuantity.setCellValueFactory(c -> c.getValue().quantityProperty());
    }

    public void LoginChangeBtnDesible() throws IOException {
        btnLogin.setDisable(true);
        btnLogout.setDisable(false);
        btnConfirmBuy.setDisable(false);
        btnEmptyCaddie.setDisable(false);
        btnDeleteArticles.setDisable(false);
        btnNextElement.setDisable(false);
        btnPreviousElement.setDisable(false);
        SpinnerQuantity.setDisable(false);
        btnBuy.setDisable(false);

        String data = "CONSULT#1";
        clientSocket.send(data);

        String reponse = clientSocket.receive();
        String[] parts = reponse.split("#");
        if(parts[1].equals("ERR")){
            System.out.println("Consult ERR !");
        } else {
            TextFieldArticle.setText(parts[3]);
            TextFieldPrice.setText(parts[4]);
            TextFieldStock.setText(parts[5]);
            ImageViewArticle.setImage(new Image(getClass().getResource("/images/" + parts[6]).toExternalForm()));

        }
    }

    public void LogoutChangeBtnDesible(){
        btnLogin.setDisable(false);
        btnLogout.setDisable(true);
        btnConfirmBuy.setDisable(true);
        btnEmptyCaddie.setDisable(true);
        btnDeleteArticles.setDisable(true);
        btnNextElement.setDisable(true);
        SpinnerQuantity.setDisable(true);
        btnPreviousElement.setDisable(true);
        btnBuy.setDisable(true);

        TextFieldArticle.setText("");
        TextFieldPrice.setText("");
        TextFieldStock.setText("");
        ImageViewArticle.setImage(null);

    }

    public int getIdElement() {
        return idElement;
    }

    public void setIdElement(int idElement) {
        if(idElement == 0){
            this.idElement = 21;
        } else if (idElement == 22) {
            this.idElement = 1;
        } else
            this.idElement = idElement;
    }
}

