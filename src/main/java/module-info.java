module com.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.udp_subscriptor to javafx.fxml;
    exports com.example.udp_subscriptor;
}