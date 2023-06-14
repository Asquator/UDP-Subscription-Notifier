package com.example.udp_subscriptor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class NotifierController {

    @FXML
    private Button subButton;

    @FXML
    private TextArea textArea;

    private boolean subscribed = false;

    NotifierClient client;

    public void initialize(){
        subButton.setDisable(true);

        try {
            client = new NotifierClient(this, UserApplication.getHostname());
        }

        catch (Exception e){
            System.err.println("Couldn't initialize client");
            e.printStackTrace();
            System.exit(1);
        }

        Thread cThread = new Thread(client);
        cThread.setDaemon(true);
        cThread.start();

        subButton.setDisable(false);
    }

    void appendText(String text){
         textArea.setText(textArea.getText() + text);
    }

    @FXML
    void onClearClicked(ActionEvent event) {
        textArea.clear();
    }

    @FXML
    void onSignClicked(ActionEvent event) {
        subscribed = !subscribed;
        subButton.setDisable(true);

        client.subscribe(subscribed);

        subButton.setText(subscribed ? "Unsubscribe" : "Subscribe");
        subButton.setDisable(false);
    }

}
