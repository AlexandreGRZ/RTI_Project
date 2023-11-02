module com.hepl.purchase.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.hepl.purchase.client to javafx.fxml;
    exports com.hepl.purchase.client;
}