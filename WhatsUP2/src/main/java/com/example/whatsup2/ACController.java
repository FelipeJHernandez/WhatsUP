package com.example.whatsup2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import static java.lang.Math.abs;

public class ACController {

    private boolean keyTaken;

    private int port;

    @FXML
    private CheckBox ARButton1;

    @FXML
    private CheckBox ARButton2;

    @FXML
    private Label AlertLabel;

    @FXML
    private TextField DireccionField;

    @FXML
    private Button GenerarCertificado;

    @FXML
    private TextField NameField;

    @FXML
    private TextField SeguridadField;

    @FXML
    private Label llaveAEnviar;

    @FXML
    void OnARButton1Click(ActionEvent event) {
        ARButton1.setSelected(true);
        ARButton2.setSelected(false);
    }

    @FXML
    void OnARButton2Click(ActionEvent event) {
        ARButton2.setSelected(true);
        ARButton1.setSelected(false);
    }

    @FXML
    void OnCertificadoClick(ActionEvent event) {
        String llave_publica = llaveAEnviar.getText();
        String Entidad;
        if (llave_publica.equals("") || keyTaken){
            AlertLabel.setText("Genera una llave pública válida para crear tu certificado digital");
        }
        if(NameField.getText().isEmpty() || DireccionField.getText().isEmpty() ||  SeguridadField.getText().isEmpty() || (!ARButton1.isSelected() && !ARButton2.isSelected())){
            AlertLabel.setText("Completa todos los campos para crear tu certificado digital");
        }
        else{
            if(ARButton1.isSelected()){
                port=11111;
                Entidad=ARButton1.getText();
            }
            else {
                port=22222;
                Entidad=ARButton2.getText();
            }
            try{
                Socket socket = new Socket("127.0.0.1",port);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("Dame un certificado");
                String comando = in.readUTF();
                if(comando.equals("Manda Datos")){
                    out.writeUTF(NameField.getText());
                    out.writeUTF(DireccionField.getText());
                    out.writeUTF(SeguridadField.getText());
                    out.writeUTF(llaveAEnviar.getText());
                    comando=in.readUTF();
                    if(comando.equals("Certificado registrado")){
                        try{
                            PrintWriter writer = new PrintWriter(NameField.getText()+".key.txt","UTF-8");
                            int FraseCifra = HASH(SeguridadField.getText());
                            int llavepriv= Integer.valueOf(llave_publica)+74;
                            String strllavepriv = Cifrar(String.valueOf(llavepriv),FraseCifra);
                            System.out.println(strllavepriv);
                            writer.println("Llave privada: "+strllavepriv);
                            writer.close();
                            writer = new PrintWriter(NameField.getText()+".req.txt","UTF-8");
                            writer.println("Nombre: "+NameField.getText());
                            writer.println("Domicilio: "+DireccionField.getText());
                            writer.println("Llave pública: "+llave_publica);
                            writer.close();
                            writer=new PrintWriter(NameField.getText()+".cert.txt","UTF-8");
                            writer.println();
                            String numcert= in.readUTF();
                            String emision=in.readUTF();
                            String vigencia=in.readUTF();
                            writer.println("ID: "+numcert);
                            writer.println("Entidad Certificadora: "+Entidad);
                            writer.println("Nombre: "+NameField.getText());
                            writer.println("Domicilio: "+DireccionField.getText());
                            writer.println("Llave pública: "+llave_publica);
                            writer.println("Fecha de Emisión: "+emision);
                            writer.println("Fecha de Vencimiento: "+vigencia);
                            writer.close();

                        }catch (Exception e){
                            AlertLabel.setText("Hubo un problema generando los archivos de certificado");
                        }
                    }else{
                        AlertLabel.setText("Ya se ha registrado un certificado a nimbre de esta persona en esta AR");
                    }
                    socket.close();
                }
            }catch (Exception e){

            }
        }
    }

    @FXML
    void OnGenerarLlavesClick(ActionEvent event) {
        Random random = new Random();
        int llave = random.nextInt();
        llaveAEnviar.setText(String.valueOf(abs(llave)));
        if(ARButton1.isSelected()) {
            try {
                Socket socket = new Socket("127.0.0.1",11111);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in =new DataInputStream(socket.getInputStream());
                out.writeUTF("Busca una llave");
                String comando = in.readUTF();
                if(comando.equals("Dame la llave")){
                    out.writeUTF(llaveAEnviar.getText());
                    comando= in.readUTF();
                    if (comando.equals("Llave libre")){
                        llaveAEnviar.setStyle("-fx-text-fill: green");
                        keyTaken=false;
                    }
                    else{
                        llaveAEnviar.setStyle("-fx-text-fill: crimson");
                        keyTaken=true;
                        AlertLabel.setText("La llave pública ya ha sido tomada");
                    }
                }
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if (ARButton2.isSelected()){
            try {
                Socket socket = new Socket("127.0.0.1",22222);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in =new DataInputStream(socket.getInputStream());
                out.writeUTF("Busca una llave");
                String comando = in.readUTF();
                if(comando.equals("Dame la llave")){
                    out.writeUTF(llaveAEnviar.getText());
                    comando= in.readUTF();
                    if (comando.equals("Llave libre")){
                        llaveAEnviar.setStyle("-fx-text-fill: green");
                        keyTaken=false;
                    }
                    else{
                        keyTaken=true;
                        AlertLabel.setText("La llave pública ya ha sido tomada");
                    }
                }
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else
        {
            AlertLabel.setText("Selecciona una agencia registradora para continuar con la generación de la llave");
        }
    }

    public int HASH(String mensaje){
        char[]mensajechararray = mensaje.toCharArray();
        int i=1;
        int resultado=0;
        for (char caracter:mensajechararray){
            resultado+=(caracter*i);
        }
        return resultado;
    }
    public String Cifrar(String mensaje, int llave){
        char[] mensajeCharArray = mensaje.toCharArray();
        StringBuilder msgBuilder = new StringBuilder();
        for(char caracter:mensajeCharArray){
            int aux=caracter;
            boolean yacifrado=false;
            if(llave>=74){
                llave=llave%74;
            }
            if(caracter>=48 && caracter<=122) {
                caracter += llave;
                yacifrado=true;
            }
            if(caracter>122 && yacifrado){
                caracter-=74;
            }
            msgBuilder.append(caracter);
            aux=caracter;
        }
        return (msgBuilder.toString());

    }
    public String Descifrar(String mensaje, int llave){
        char[] mensajeCharArray = mensaje.toCharArray();

        StringBuilder msgBuilder = new StringBuilder();
        for(char caracter:mensajeCharArray){
            int aux=caracter;
            boolean yadescifrado=false;

            if(llave>=74){
                llave=llave%74;
            }
            if(caracter>=48 && caracter<=122) {
                caracter -= llave;
                yadescifrado=true;
                if(caracter<48 && yadescifrado){
                    caracter+=74;
                }
            }
            aux=caracter;
            msgBuilder.append(caracter);
        }
        return (msgBuilder.toString());
    }
}
