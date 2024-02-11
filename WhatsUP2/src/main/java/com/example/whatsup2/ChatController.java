package com.example.whatsup2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class ChatController implements Initializable{
    List<List<String>> mensajes=new ArrayList<>();
    Cliente cliente;

    Cliente clienteContacto;

    int encription=0;

    @FXML
    private TextField FraseSeguridad;

    @FXML
    private Label ContactLabel;

    @FXML
    private Label LlaveUsuario;


    @FXML
    private TextField PrivateKField;

    @FXML
    private RadioButton Asim;

    @FXML
    private RadioButton Firma;

    @FXML
    private VBox Messages;

    @FXML
    private RadioButton Sim;

    @FXML
    private RadioButton Sobre;

    @FXML
    private RadioButton TP;

    @FXML
    private TextField msgField;

    Thread hilo;

    ServerSocket socket;
    public void IniciarHilo(){
        hilo=new Thread(()->{
            try{
                socket=new ServerSocket(cliente.returnPort());
                while (true){
                    Socket sc = socket.accept();
                    DataInputStream in = new DataInputStream(sc.getInputStream());
                    String mensaje = in.readUTF();
                    if("Nuevo Mensaje".equals(mensaje))
                    {
                        Platform.runLater(()->{
                            updateScene();
                        });
                    }
                    sc.close();
                }
            }
            catch (Exception e){
                System.out.println("No se pudieron conseguir los mensajes");
            }
        });
        hilo.start();
    }

    @FXML
    void OnCAClick(ActionEvent event) {
        encription=2;
        Sim.setSelected(false);
        Sobre.setSelected(false);
        TP.setSelected(false);
        Firma.setSelected(false);



    }

    @FXML
    void OnCSClick(ActionEvent event) {
        encription=1;
        Asim.setSelected(false);
        Sobre.setSelected(false);
        TP.setSelected(false);
        Firma.setSelected(false);

    }
    @FXML
    void OnSignClick(ActionEvent event) {
        encription=3;
        Sim.setSelected(false);
        Sobre.setSelected(false);
        TP.setSelected(false);
        Asim.setSelected(false);
    }

    @FXML
    void OnSobreClick(ActionEvent event) {
        encription=4;
        Sim.setSelected(false);
        Asim.setSelected(false);
        TP.setSelected(false);
        Firma.setSelected(false);
    }

    @FXML
    void OnTPClick(ActionEvent event) {
        encription=0;
        Sim.setSelected(false);
        Sobre.setSelected(false);
        Asim.setSelected(false);
        Firma.setSelected(false);
    }
    @FXML
    void OnSendClick(ActionEvent event) {
        try {
            String mensaje=msgField.getText();
            Socket socket = new Socket("127.0.0.1",12345);
            DataInputStream in= new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            if (!mensaje.isEmpty())
            {
                out.writeUTF("Manda Mensaje");
                String comando=in.readUTF();
                if(comando.equals("Manda Datos")) {
                    switch (encription) {
                        case 0 -> {
                            out.writeUTF(mensaje);
                            out.writeUTF(cliente.returnCel());
                            out.writeUTF(clienteContacto.returnCel());
                            out.writeUTF("TP");
                            out.writeUTF("");
                            String comando2 =in.readUTF();
                            if(comando2.equals("Manda Puerto Contacto")){
                                out.writeInt(clienteContacto.returnPort());
                            }
                            break;
                        }
                        case 1 -> {
                                int Key = Integer.parseInt(LlaveUsuario.getText());
                                mensaje = Cifrar(mensaje, Key);
                                out.writeUTF(mensaje);
                                out.writeUTF(cliente.returnCel());
                                out.writeUTF(clienteContacto.returnCel());
                                out.writeUTF("SIM");
                                out.writeUTF("");
                                String comando2 = in.readUTF();
                                if(comando2.equals("Manda Puerto Contacto")){
                                    out.writeInt(clienteContacto.returnPort());
                                }
                            break;
                        }
                        case 2 -> {
                            int Key = Integer.valueOf(Descifrar(PrivateKField.getText(),HASH(FraseSeguridad.getText())));
                            mensaje = Cifrar(mensaje,Key);
                            out.writeUTF(mensaje);
                            out.writeUTF(cliente.returnCel());
                            out.writeUTF(clienteContacto.returnCel());
                            out.writeUTF("ASIM");
                            out.writeUTF("");
                            String comando2 = in.readUTF();
                            if(comando2.equals("Manda Puerto Contacto")){
                                out.writeInt(clienteContacto.returnPort());
                            }
                            break;
                        }
                        case 3 -> {
                            int firma = Integer.valueOf(Descifrar(PrivateKField.getText(),HASH(FraseSeguridad.getText())));
                            int resumen = HASH(mensaje);
                            String firmaDigital=FirmaDigital(resumen,firma);
                            out.writeUTF(mensaje);
                            out.writeUTF(cliente.returnCel());
                            out.writeUTF(clienteContacto.returnCel());
                            out.writeUTF("FIRMA");
                            out.writeUTF(firmaDigital);
                            String comando2 = in.readUTF();
                            if(comando2.equals("Manda Puerto Contacto")){
                                out.writeInt(clienteContacto.returnPort());
                            }
                            break;
                        }
                        case 4 -> {
                            int firma = Integer.valueOf(Descifrar(PrivateKField.getText(),HASH(FraseSeguridad.getText())));
                            int resumen=HASH(mensaje);
                            String firmaDigital=FirmaDigital(resumen,firma);
                            int LlavePublica = Integer.parseInt(clienteContacto.returnPK());
                            String[] Resultado = SobreDigital(mensaje,firmaDigital,LlavePublica);
                            mensaje=Resultado[0];
                            firmaDigital=Resultado[1];
                            String Seguridad = Resultado[2];
                            out.writeUTF(mensaje);
                            out.writeUTF(cliente.returnCel());
                            out.writeUTF(clienteContacto.returnCel());
                            out.writeUTF(Seguridad);
                            out.writeUTF(firmaDigital);
                            String comando2 = in.readUTF();
                            if(comando2.equals("Manda Puerto Contacto")){
                                out.writeInt(clienteContacto.returnPort());
                            }
                            break;
                        }
                        default -> {
                            break;
                        }
                    }
                    socket.close();
                    updateScene();
                }
            }

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void OnReturnClick(ActionEvent event){
        System.out.println("");
    }


    public void setCliente(Cliente cliente){
        this.cliente=cliente;
        IniciarHilo();
    }
    public void setClienteContacto(Cliente clienteContacto){this.clienteContacto=clienteContacto;}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO
        Thread hilo2 = new Thread(()->{
            Platform.runLater(()->{
                try{
                    System.out.println(clienteContacto.returnName());
                    TP.setSelected(true);
                    LlaveUsuario.setText(cliente.returnPK());
                    ContactLabel.setText(clienteContacto.returnName());

                }catch (Exception e){
                    e.printStackTrace();
                }
                updateScene();
            });
        });
        hilo2.start();
    }
    public String Cifrar(String mensaje, int llave){
        char[] mensajeCharArray = mensaje.toCharArray();
        StringBuilder msgBuilder = new StringBuilder();
        for(char caracter:mensajeCharArray){
            int aux=caracter;
            System.out.println(aux);
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
            System.out.println(aux);
        }
        return (msgBuilder.toString());

    }
    public String Descifrar(String mensaje, int llave){
        char[] mensajeCharArray = mensaje.toCharArray();

        StringBuilder msgBuilder = new StringBuilder();
        for(char caracter:mensajeCharArray){
            int aux=caracter;
            boolean yadescifrado=false;

            System.out.println(aux);
            if(llave>=74){
                llave=llave%74;
                System.out.println("Llave:"+llave);
            }
            if(caracter>=48 && caracter<=122) {
                caracter -= llave;
                yadescifrado=true;
                if(caracter<48 && yadescifrado){
                    caracter+=74;
                }
            }
            aux=caracter;
            System.out.println(aux);
            msgBuilder.append(caracter);
        }
        return (msgBuilder.toString());
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

    public String FirmaDigital(int resumen, int firma){
        String resumencifrado=Cifrar(String.valueOf(resumen),firma);
        return resumencifrado;
    }

    public String[] SobreDigital(String mensaje, String FirmaDigital, int destinatario){
        //LSA: Llave Simétrica Aleatoria
        Random rand = new Random();
        int LSA = rand.nextInt(74);
        LSA+=1;
        mensaje=Cifrar(mensaje,LSA);
        FirmaDigital=Cifrar(FirmaDigital,LSA);
        String LSA2 = Cifrar(String.valueOf(LSA),destinatario);
        System.out.println("LSA:"+LSA);
        String[] ResultArray =new String[]{mensaje,FirmaDigital,LSA2};
        return ResultArray;
    }
    public void updateScene(){
        try {
            msgField.clear();
            Socket sc = new Socket("127.0.0.1",12345);
            DataInputStream in = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            System.out.println("Bienvenido al chat");
            out.writeUTF("Consigue Mensajes");
            String comando=in.readUTF();
            System.out.println(comando);
            if(comando.equals("Manda Datos")){
                System.out.println("Manda tus datos");
                out.writeUTF(cliente.returnCel());
                out.writeUTF(clienteContacto.returnCel());
                ObjectInputStream oin = new ObjectInputStream(sc.getInputStream());
                mensajes.clear();
                mensajes = (List<List<String>>)oin.readObject();
                for(List<String> algo: mensajes){
                    System.out.println(algo);
                }
            }
            else{
                System.out.println("No se consiguieron los mensajes");
            }
            sc.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Messages.getChildren().clear();
        for(List mensaje: mensajes) {
            String Emisor = (String) mensaje.get(0);
            String Receptor = (String) mensaje.get(1);
            String Mensaje = (String) mensaje.get(2);
            String Seguridad = (String) mensaje.get(3);
            HBox message = new HBox();
            Label sender = new Label();
            Label msg = new Label();
            if (Emisor.equals(cliente.returnCel())) {
                sender.setStyle("-fx-text-fill: black;");
                sender.setText(cliente.returnName() + ":");
                msg.setText(Mensaje);
                message.getChildren().add(sender);
                message.getChildren().add(msg);
            } else {
                sender.setStyle("-fx-text-fill: crimson;");
                sender.setText(clienteContacto.returnName() + ":");
                msg.setText(Mensaje);
                message.getChildren().add(sender);
                message.getChildren().add(msg);
            }
            switch (Seguridad){
                case "TP"->{
                    break;
                }
                case "SIM"->{
                    msg.setStyle("-fx-background-color: magenta");
                    Button DescifraSim = new Button("Descifrar");
                    DescifraSim.setOnAction((ActionEvent ev1)->{
                        int llave = Integer.parseInt(clienteContacto.returnPK());
                        msg.setText(Descifrar(msg.getText(),llave));
                        DescifraSim.setVisible(false);
                    });
                    message.getChildren().add(DescifraSim);
                }
                case "ASIM"->{
                    msg.setStyle("-fx-background-color: green");
                    Button DescifraSim = new Button("Descifrar");
                    DescifraSim.setOnAction((ActionEvent ev1)->{
                        int llave = Integer.parseInt(clienteContacto.returnPK());
                        msg.setText(Descifrar(msg.getText(),llave));
                        DescifraSim.setVisible(false);
                    });
                    message.getChildren().add(DescifraSim);
                }
                case "FIRMA"->{
                    msg.setStyle("-fx-background-color: cyan");
                    String FD = (String) mensaje.get(4);
                    Label MeantFD = new Label();
                    MeantFD.setText(FD);
                    Label ConfirmarFD = new Label();
                    Button AveriguarFD = new Button("Verificar");
                    AveriguarFD.setOnAction((ActionEvent ev2)->{
                        int firma = Integer.parseInt(clienteContacto.returnPK());
                        int hashResult=HASH(Mensaje);
                        ConfirmarFD.setText(Cifrar(String.valueOf(hashResult),firma));
                        if (ConfirmarFD.getText().equals(FD)){
                            ConfirmarFD.setStyle("-fx-background-color: green");
                        }
                        else{
                            ConfirmarFD.setStyle("-fx-background-color: red");
                        }
                    });
                    message.getChildren().add(MeantFD);
                    message.getChildren().add(ConfirmarFD);
                    message.getChildren().add(AveriguarFD);
                }
                default -> {
                    String Firma = (String) mensaje.get(4);
                    msg.setStyle("-fx-background-color: yellow");
                    Button Desenvolver = new Button("Desensobretar");
                    Desenvolver.setOnAction((ActionEvent e3)->{
                        int LSA = Integer.parseInt(Descifrar(Seguridad,Integer.parseInt(Descifrar(PrivateKField.getText(),HASH(FraseSeguridad.getText())))));
                        String msg_aux = Descifrar(Mensaje,LSA);
                        msg.setText(msg_aux);
                    });
                    message.getChildren().add(Desenvolver);
                }
            }
            Messages.getChildren().add(message);

        }
    }
}
