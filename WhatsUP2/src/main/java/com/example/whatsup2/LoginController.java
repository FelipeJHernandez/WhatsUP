package com.example.whatsup2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LoginController {
    private Cliente cliente;

    @FXML
    private TextField Password;

    @FXML
    private TextField UserPhone;

    @FXML
    private Label WarningLabel;

    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

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
    void OnLoginClick(ActionEvent event) throws IOException {
            String Phone=UserPhone.getText();
            String psw=Password.getText();
            if(Phone.isEmpty() || psw.isEmpty()){
                System.out.println("Completa los datos para acceder");
            }
            else{
                File cert = new File(psw);
                String[]extensiones = psw.split("\\.",3);
                if(extensiones[1].equals("cert")) {
                    Scanner fileReader = new Scanner(cert);
                    while (fileReader.hasNextLine()) {
                        String line = fileReader.nextLine();
                        String[] values = line.split(": ");
                        if (values[0].equals("Llave pública")) {
                            psw = values[1];
                            System.out.println(psw);
                            break;
                        }
                    }
                    try {
                        Socket socket = new Socket("127.0.0.1", 12345);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        out.writeUTF("Login");
                        String Respuesta = in.readUTF();
                        if (Respuesta.equals("Manda Datos")) {
                            out.writeUTF(Phone);
                            out.writeUTF(psw);
                            String Login_Answer = in.readUTF();
                            if (Login_Answer.equals("Usuario Correcto")) {
                                String nombre = in.readUTF();
                                int port = in.readInt();
                                WarningLabel.setText("BIENVENIDO " + nombre);
                                cliente.setName(nombre);
                                cliente.setPhone(Phone);
                                cliente.setPort(port);
                                cliente.setPK(psw);
                                ((Node) (event.getSource())).getScene().getWindow().hide();
                                Stage stage = new Stage();
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("Contacts-View.fxml"));
                                Parent root = loader.load();
                                ContactsController contactsController = loader.getController();
                                System.out.println(cliente.returnName());
                                contactsController.setCliente(cliente);
                                Scene scene = new Scene(root);
                                stage.setTitle("Contactos");
                                stage.setScene(scene);
                                stage.show();
                            } else if (Login_Answer.equals("Usuario Inexistente")) {
                                WarningLabel.setText("Las credenciales introducidas no son correctas");
                                Password.clear();
                                UserPhone.clear();
                            } else {
                                System.out.println("Yo tampoco lo entiendo");
                            }
                            socket.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    }
    public void setCliente (Cliente cliente){this.cliente=cliente;}
}
