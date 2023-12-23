package com.example.paymentclientsecure;

import com.example.paymentclientsecure.Modele.SharedDataModelSecure;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class numberCardControllerSecure {

    public TextField TFNumberOfCard;
    private SharedDataModelSecure sharedDataModel;


    public void onActionAchterBtn(ActionEvent actionEvent) {
        String cardNumber = TFNumberOfCard.getText();
        sharedDataModel.setCardNumber(cardNumber);
        closeWindow();

    }
    public void setSharedDataModel(SharedDataModelSecure sharedDataModel) {
        this.sharedDataModel = sharedDataModel;
    }

    private void closeWindow() {
        Scene scene = TFNumberOfCard.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();
    }

}
