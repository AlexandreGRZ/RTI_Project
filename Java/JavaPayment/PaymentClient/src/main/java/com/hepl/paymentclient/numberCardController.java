package com.hepl.paymentclient;

import com.hepl.paymentclient.Modele.SharedDataModel;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class numberCardController {

    public TextField TFNumberOfCard;
    private SharedDataModel sharedDataModel;


    public void onActionAchterBtn(ActionEvent actionEvent) {
        String cardNumber = TFNumberOfCard.getText();
        sharedDataModel.setCardNumber(cardNumber);
        closeWindow();

    }
    public void setSharedDataModel(SharedDataModel sharedDataModel) {
        this.sharedDataModel = sharedDataModel;
    }

    private void closeWindow() {
        Scene scene = TFNumberOfCard.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();
    }

}
