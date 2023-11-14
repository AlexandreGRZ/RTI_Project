module com.hepl.purchaseclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hepl.purchaseclient to javafx.fxml;
    opens com.hepl.purchaseclient.model to javafx.base;

    exports com.hepl.purchaseclient;
}