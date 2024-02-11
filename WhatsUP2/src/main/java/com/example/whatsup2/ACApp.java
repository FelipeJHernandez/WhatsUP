package com.example.whatsup2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.stage.Stage;

import java.io.IOException;

public class ACApp extends Application {
    AR1 ar1 = new AR1();
    AR2 ar2 = new AR2();
    ARC arc = new ARC();
    public ACApp(){
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AC-View.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        ACController acController=(ACController)fxmlLoader.getController();
        Thread iniciaAR1= new Thread(()->{
            ar1.run();
        });
        Thread iniciaAR2= new Thread(()->{
            ar2.run();
        });
        Thread iniciaARC= new Thread(()->{
            arc.run();
        });
        iniciaAR1.start();
        iniciaAR2.start();
        iniciaARC.start();
        Scene scene = new Scene(root);
        stage.setTitle("Agencia Certificadora");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
