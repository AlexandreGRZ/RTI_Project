module com.hepl.paymentclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.hepl.protocol.requests; // Ajoute cette ligne pour déclarer la dépendance

    opens com.hepl.paymentclient to javafx.fxml;
    exports com.hepl.paymentclient;
}