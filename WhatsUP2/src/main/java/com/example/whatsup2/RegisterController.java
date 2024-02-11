package com.example.whatsup2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class RegisterController implements Initializable {

    @FXML
    private TextField Password;

    @FXML
    private TextField UserName;

    @FXML
    private TextField UserPhon;

    @FXML
    private Label WarningLabel;

    Cliente cliente;


    @FXML
    void OnExitClick(ActionEvent event) throws  IOException{
        Stage stage1 = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HelloController HelloController = loader.getController();
        Scene scene = new Scene(root);

        stage1.setTitle("Hello!");
        stage1.setScene(scene);
        stage1.show();
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    void OnRegisterClick(ActionEvent event) throws IOException{
        String nombre=UserName.getText();
        String phone=UserPhon.getText();
        String password=Password.getText();
        if(password.isEmpty()||phone.isEmpty()||nombre.isEmpty()){
            WarningLabel.setText("Es necesario llenar todos los campos para crear tu cuenta de Whats-UP");
        }

        else{
            File cert = new File(password);
            String[]extensiones = password.split("\\.",3);
            if(extensiones[1].equals("cert"))
            {
                Scanner fileReader = new Scanner(cert);
                while (fileReader.hasNextLine()) {
                    String line = fileReader.nextLine();
                    String[] values = line.split(": ");
                    if (values[0].equals("Llave pública")){
                        password=values[1];
                        System.out.println(password);
                        break;
                    }
                }
                try {
                    Socket socket = new Socket("127.0.0.1", 12345);
                    System.out.println("Conectando con el server...");
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    out.writeUTF("Register");
                    String Respuesta = in.readUTF();
                    if (Respuesta.equals("Manda Datos")) {
                        System.out.println("Ha respondido el server");
                        out.writeUTF(nombre);
                        out.writeUTF(phone);
                        out.writeUTF(password);
                        String Register_Answer = in.readUTF();
                        if (Register_Answer.equals("Usuario ya registrado")) {
                            WarningLabel.setText("Ya hay un usuario registrado con ese nombre y/o teléfono");
                            socket.close();
                        } else if (Register_Answer.equals("Nuevo Usuario")) {
                            WarningLabel.setText("Registrado Exitosamente");
                            socket.close();
                            Stage stage1 = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login-View.fxml"));
                            Parent root = null;
                            try {
                                root = loader.load();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            LoginController loginController = loader.getController();
                            Scene scene = new Scene(root);
                            loginController.setCliente(cliente);
                            stage1.setTitle("Login");
                            stage1.setScene(scene);
                            stage1.show();
                            ((Node) (event.getSource())).getScene().getWindow().hide();

                        } else {
                            System.out.println("Estoy tan confundido como tú");
                            socket.close();
                        }
                    } else {
                        System.out.println("Esto no funciona");
                        socket.close();
                    }
                } catch (Exception e) {
                    WarningLabel.setText("Fallo en la conexión con el server");
                    e.printStackTrace();
                }
            }
            else{
                WarningLabel.setText("Introduzca una ruta a un archivo de certificado válido");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO
    }

    public void setCliente(Cliente cliente){this.cliente=cliente;}
}
