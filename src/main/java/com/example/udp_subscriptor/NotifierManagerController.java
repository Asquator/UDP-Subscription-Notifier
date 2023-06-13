package com.example.udp_subscriptor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class NotifierManagerController {

    @FXML
    private TextArea textArea;

    private NotifierServer server;

    @FXML
    void onSendClicked(ActionEvent event) {
        server.send(textArea.getText());
    }

    public void initialize(){
        server = new NotifierServer();
        server.start();
    }

}
