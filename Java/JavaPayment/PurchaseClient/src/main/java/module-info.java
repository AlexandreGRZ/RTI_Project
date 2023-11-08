module com.hepl.purchaseclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hepl.purchaseclient to javafx.fxml;
    exports com.hepl.purchaseclient;
}