package com.hepl.clientachat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientAchatApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientAchatApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 780, 620);
        stage.setTitle("Le Maraicher en ligne");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}