package com.example.whatsup2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    public Cliente cliente=new Cliente("","",0,"");

    @FXML
    private Button Login;

    @FXML
    private Button Register;

    @FXML
    void onLoginButtonClick(ActionEvent event) throws IOException {
        ((Node)(event.getSource())).getScene().getWindow().hide();
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login-View.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        Scene scene = new Scene(root);
        loginController.setCliente(cliente);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void onRegisterButtonClick(ActionEvent event) throws  IOException{
        ((Node)(event.getSource())).getScene().getWindow().hide();
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Register-View.fxml"));
        Parent root = loader.load();
        RegisterController registerController = loader.getController();
        Scene scene = new Scene(root);
        registerController.setCliente(cliente);
        stage.setTitle("Registrar");
        stage.setScene(scene);
        stage.show();

    }
    public void setCliente(Cliente cliente){this.cliente=cliente;}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO
    }
}
