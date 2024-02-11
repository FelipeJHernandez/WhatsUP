package com.example.whatsup2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public HelloApplication(){
    }
    @Override
    public void start(Stage stage) throws IOException {
        Cliente cliente = new Cliente("", "", 0,"");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        HelloController helloController=(HelloController)fxmlLoader.getController();
        helloController.setCliente(cliente);
        Scene scene = new Scene(root);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}