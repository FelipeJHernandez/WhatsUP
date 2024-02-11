package com.example.whatsup2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class ServerController {
//
    public boolean interfere=false;

    @FXML
    private Label Interfeer_label;

    @FXML
    private TextField Modified_Message;

    @FXML
    private VBox Operaciones;

    @FXML
    void ButtonPressed(ActionEvent event) {
        Button button= (Button)event.getSource();
        if("Interferir mensajes".equals(button.getText())){
            if(interfere==false) {
                Interfeer_label.setText("SI");
                Interfeer_label.setStyle("-fx-text-fill: crimson;");
            }
            if(interfere==true){
                Interfeer_label.setText("No");
                Interfeer_label.setStyle("-fx-text-fill: black;");
            }
            interfere= !interfere;
        }
    }
    public boolean getInterfere(){
        return interfere;
    }
    String getMessage(){
        return Modified_Message.getText();
    }

    public void Write(String mensaje){
        Label label = new Label();
        label.setText(mensaje);
        Operaciones.getChildren().add(label);
    }

}
