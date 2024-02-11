package com.example.whatsup2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class ContactsController implements Initializable {
    List<String> contactos =new ArrayList<>();
    Cliente cliente;
    ServerSocket socket = null;

    private Cliente clienteContacto = new Cliente("","",0,"");
    private  Thread ObtenerContactos;

    @FXML
    private VBox Contacts;



    @FXML
    void AddContactClick(ActionEvent event) throws IOException{
        try {
            VBox vbox = new VBox(10);
            TextField PhoneInput = new TextField();
            PhoneInput.setPromptText("Teléfono");
            TextField NameInput = new TextField();
            NameInput.setPromptText("Nombre");
            Label Instrucciones = new Label("Ingrese el número y nombre del contacto");
            Label Warning= new Label("");
            Button agregar = new Button("Agregar");
            vbox.getChildren().addAll(Instrucciones,NameInput,PhoneInput,Warning,agregar);
            vbox.setAlignment(Pos.CENTER);
            Scene scene= new Scene(vbox,300,150);
            Stage stage =new Stage();
            stage.setScene(scene);
            stage.show();
            agregar.setOnAction((ActionEvent e)-> {
                String numero = PhoneInput.getText();
                String nombre = NameInput.getText();
                try{
                    Socket sc = new Socket("127.0.0.1",12345);
                    DataOutputStream out = new DataOutputStream(sc.getOutputStream());
                    DataInputStream in = new DataInputStream(sc.getInputStream());
                    out.writeUTF("Busca Contacto");
                    String comando = in.readUTF();
                    if(comando.equals("Manda Datos")){
                        out.writeUTF(numero);
                        out.writeUTF(nombre);
                        comando=in.readUTF();
                        if(comando.equals("Usuario Encontrado")){
                            System.out.println("a");
                            out.writeUTF(cliente.returnCel());
                            String Actualiza=in.readUTF();
                            if(Actualiza.equals("Contacto Agregado")){
                                UpdateScene();
                                stage.close();
                            }
                            else if (Actualiza.equals("Contacto ya Agregado")) {
                                UpdateScene();
                                stage.close();
                            }
                        }
                        else{
                            Warning.setText("Ningún usuario coincide con esos datos");
                        }
                    }
                    sc.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        }catch (Exception e2){
            e2.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("iniciando vista");
        Thread hilo=new Thread(()->{
            Platform.runLater(()->{
                UpdateScene();
            });
        });
        hilo.start();
    }
    public void IniciarHilo(){
        ObtenerContactos=new Thread(()->{
            int port=cliente.returnPort();
            try{
                socket=new ServerSocket(port);
                while (true){
                    Socket personalSocket= socket.accept();
                    DataInputStream in = new DataInputStream(personalSocket.getInputStream());
                    String comando= in.readUTF();
                    if(comando.equals("Nuevo Contacto")){
                        Platform.runLater(()->{
                            UpdateScene();
                        });
                    }
                    personalSocket.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        ObtenerContactos.start();
    }

    public void SetCliente(Cliente cliente){
        this.cliente=cliente;
        Platform.runLater(()->{
        });
        IniciarHilo();
    }
    /**
     *
     * @param event
     * @param contacto
     * @throws IOException
     */
    private void EntraChat(ActionEvent event, String contacto) throws  IOException{
        Stage stage1= new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Chat-View.fxml"));
        Parent root = loader.load();
        String celular="";
        String Pub_K="";
        int PORT = 0;
        try {
            Socket sc = new Socket("127.0.0.1",12345);
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            DataInputStream in = new DataInputStream(sc.getInputStream());
            out.writeUTF("Inicia Chat");
            String comando_server=in.readUTF();
            if("Manda Datos".equals(comando_server)){
                out.writeUTF(contacto);
                out.writeUTF(cliente.returnName());
                celular=in.readUTF();
                PORT = in.readInt();
                Pub_K=in.readUTF();
                System.out.println(PORT);
                clienteContacto.setPhone(celular);
                clienteContacto.setPort(PORT);
                clienteContacto.setPK(Pub_K);
                ObtenFirmaContacto();
                System.out.println("Logrado!");
            }
            sc.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        clienteContacto.setName(contacto);
        ChatController chatController= loader.getController();
        chatController.setCliente(cliente);
        System.out.println(clienteContacto);
        chatController.setClienteContacto(clienteContacto);
        Scene scene1 = new Scene(root);
        stage1.setTitle(contacto);
        stage1.setScene(scene1);
        stage1.show();
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }
    private void UpdateScene(){
        try{
            System.out.println("Consiguiendo contactos");
            Socket sc = new Socket("127.0.0.1",12345);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            out.writeUTF("Muestra contactos");
            contactos.clear();
            String comando=in.readUTF();
            if((comando).equals("Manda Datos")){
                out.writeUTF(cliente.returnCel());
                System.out.println("enviando solicitud...");
                ObjectInputStream oin = new ObjectInputStream(sc.getInputStream());
                contactos = (List<String>) oin.readObject();
            }
            sc.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        Contacts.getChildren().clear();
        System.out.println(contactos);
        for (String contacto : contactos){
            Button botonContacto = new Button(contacto);
            botonContacto.setOnAction((ActionEvent e1)->{
                String nombre=botonContacto.getText();
                System.out.println(nombre);
                try{
                    if(socket!=null) {
                        socket.close();
                        socket=null;
                    }
                    if(ObtenerContactos != null && ObtenerContactos.isAlive()){
                        ObtenerContactos.interrupt();
                    }
                    EntraChat(e1,nombre);
                }catch (Exception e2){
                    e2.printStackTrace();
                }
            });
            Contacts.getChildren().add(botonContacto);
        }
    }

    public void setCliente(Cliente cliente){this.cliente=cliente;}

    public void ObtenFirmaContacto(){
        try{
            Socket socket = new Socket("127.0.0.1",12345);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Consigue Firma");
            String respuesta = in.readUTF();
            if(respuesta.equals("Manda datos")){
                out.writeUTF(clienteContacto.returnPK());
                clienteContacto.setPK(in.readUTF());
                System.out.println(clienteContacto.returnPK());
            }
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
