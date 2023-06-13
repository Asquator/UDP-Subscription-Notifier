package com.example.udp_subscriptor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserApplication extends Application {

    private static String hostname;

    public static String getHostname() {
        return hostname;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserApplication.class.getResource("notifier_user.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Notifier");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        if(args.length < 1)
            throw new RuntimeException("Provide server hostname in CLI arguments!\n" +
            "For example : localhost");

        hostname = args[0];

        launch();
    }
}