module com.hepl.paymentclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hepl.paymentclient to javafx.fxml;
    exports com.hepl.paymentclient;
}