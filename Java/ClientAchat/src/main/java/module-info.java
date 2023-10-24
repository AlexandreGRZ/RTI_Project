module com.hepl.clientachat {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.hepl.clientachat to javafx.fxml;
    exports com.hepl.clientachat;
}