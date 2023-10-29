module com.hepl.clientachat {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.hepl.ClientAchat to javafx.fxml;
    exports com.hepl.ClientAchat;
}