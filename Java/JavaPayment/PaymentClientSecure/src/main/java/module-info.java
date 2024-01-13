module com.example.paymentclientsecure {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.hepl.protocol.requests;
    requires org.bouncycastle.provider;
    requires com.hepl.paymentclient;


    opens com.example.paymentclientsecure to javafx.fxml;
    exports com.example.paymentclientsecure;
}