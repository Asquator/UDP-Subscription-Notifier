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
        textArea.clear();
    }

    /**
     * Initializes notifier manager
     */
    public void initialize(){
        //start the server thread
        server = new NotifierServer();
        server.setDaemon(true);
        server.start();
    }

}
