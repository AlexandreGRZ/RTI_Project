package com.hepl.paymentclient;


import com.hepl.model.Facture;
import com.hepl.paymentclient.Modele.SharedDataModel;
import com.hepl.protocol.requests.GetFacturesRequest;
import com.hepl.protocol.requests.LoginRequest;
import com.hepl.protocol.requests.LogoutRequest;
import com.hepl.protocol.requests.PayFactureRequest;
import com.hepl.protocol.responses.GetFacturesResponse;
import com.hepl.protocol.responses.LoginResponse;
import com.hepl.protocol.responses.PayFactureResponse;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketOption;
import java.net.URL;
import java.util.ResourceBundle;

public class PayementController implements Initializable {
    public Button btnLogout;
    @FXML
    public Button btnLogin;
    @FXML
    public TextField TextFieldPassword;
    @FXML
    public TextField TextFieldLogin;
    @FXML
    public TableView TVFacture;
    @FXML
    public TableColumn TCNom;
    @FXML
    public TableColumn TCMontantAPayer;
    @FXML
    public TableColumn TCDate;
    @FXML
    public Button btnPayer;
    @FXML
    public Button btnRechercher;
    @FXML
    public TextField TxtFieldIdClient;
    public TableColumn TCpaye;
    @FXML
    private Label welcomeText;

    private Socket socket;
    private ObjectInputStream Input;
    private ObjectOutputStream Output;

    private ObservableList<Facture> facturesToDisplay;

    private SharedDataModel sharedDataModel;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        try {
//            socket = new Socket("127.0.0.1", 8080);
//            System.out.println();
//            Output = new ObjectOutputStream(socket.getOutputStream());
//            Input = new ObjectInputStream(socket.getInputStream());
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

    public void onLoginClick(ActionEvent actionEvent) throws IOException, ClassNotFoundException {

        LoginRequest loginRequest = new LoginRequest(TextFieldLogin.getText(), TextFieldPassword.getText());
        Output.writeObject(loginRequest);

        LoginResponse loginResponse = (LoginResponse) Input.readObject();

        System.out.println("login status : " + loginResponse.isSuccess());
        ChangeLoginInterface();
    }

    public void onLogoutClick(ActionEvent actionEvent) throws IOException {

        LogoutRequest logoutRequest = new LogoutRequest();
        Output.writeObject(logoutRequest);
        ChangeLogOutInterface();
    }
    
    public void onActionRechercherBtn(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        
        int idClientToSearch = Integer.parseInt(TxtFieldIdClient.getText());

        GetFacturesRequest getFacturesRequest = new GetFacturesRequest(idClientToSearch);

        Output.writeObject(getFacturesRequest);

        GetFacturesResponse getFacturesResponse = (GetFacturesResponse) Input.readObject();

        setFacturesToDisplay(FXCollections.observableArrayList());
        for (Facture f : getFacturesResponse.getFactures()) {
            getFacturesToDisplay().add(f);
        }

        TCNom.setCellValueFactory(new PropertyValueFactory<>("id"));
        TCDate.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TCMontantAPayer.setCellValueFactory(new PropertyValueFactory<>("date"));
        TCpaye.setCellValueFactory(new PropertyValueFactory<>("payed"));

        TVFacture.setItems(getFacturesToDisplay());
        
    }

    public void onActionPayerBtn(ActionEvent actionEvent) {

        if (sharedDataModel == null) {
            sharedDataModel = new SharedDataModel();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("numberCardDemand.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        numberCardController numberCardController = loader.getController();

        numberCardController.setSharedDataModel(sharedDataModel);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Number Card Request");
        stage.setScene(scene);

        stage.setOnHiding(event -> {
            try {
                handleWindow2Closed();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        stage.show();
    }
    public void ChangeLoginInterface(){
        btnLogin.setDisable(true);
        btnLogout.setDisable(false);
        btnRechercher.setDisable(false);
        btnPayer.setDisable(false);
    }

    public void ChangeLogOutInterface(){
        btnLogin.setDisable(false);
        btnLogout.setDisable(true);
        btnRechercher.setDisable(true);
        btnPayer.setDisable(true);

    }

    public ObservableList<Facture> getFacturesToDisplay() {
        return facturesToDisplay;
    }

    public void setFacturesToDisplay(ObservableList<Facture> facturesToDisplay) {
        this.facturesToDisplay = facturesToDisplay;
    }

    public void setSharedDataModel(SharedDataModel sharedDataModel) {
        this.sharedDataModel = sharedDataModel;
    }

    private void handleWindow2Closed() throws IOException, ClassNotFoundException {
        Facture factureAPayer = (Facture) TVFacture.getSelectionModel().getSelectedItem();

        PayFactureRequest payFactureRequest = new PayFactureRequest(factureAPayer.getId(),sharedDataModel.getCardNumber());

        Output.writeObject(payFactureRequest);

        PayFactureResponse payFactureResponse = (PayFactureResponse) Input.readObject();

        if(payFactureResponse.isSuccess()){
            System.out.println("Réussis !");
        }   else{
            System.out.println("Raté !");
        }

    }
}