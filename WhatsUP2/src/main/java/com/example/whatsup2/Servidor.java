package com.example.whatsup2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Servidor implements Runnable {
    BD_Connect conn = BD_Connect.GetInstancia();
    Connection conexion = conn.CreateConnection();
    static ServerController myController;


    @Override
    public void run() {
        ServerSocket servidor = null;
        Socket socket = null;
        DataInputStream in;
        int puerto = 12345;
        try{
            servidor = new ServerSocket(puerto);
            System.out.println("Servidor iniciado");
            while(true){
                socket = servidor.accept();
                in = new DataInputStream(socket.getInputStream());
                System.out.println(servidor);
                System.out.println(socket);
                String comando = in.readUTF();
                System.out.println(comando);
                switch (comando){
                    case "Login"-> {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Manda Datos");
                        String phone = in.readUTF();
                        String password = in.readUTF();
                        password=EncriptaPSW(password);
                        PreparedStatement query = conexion.prepareStatement("SELECT * FROM users where Phone_Number=? and Llave_publica=?;");
                        query.setString(1,phone);
                        query.setString(2,password);
                        ResultSet query_result=query.executeQuery();
                        if(query_result.next()){
                            out.writeUTF("Usuario Correcto");
                            out.writeUTF(query_result.getString("Username"));
                            out.writeInt(query_result.getInt("PORT"));
                        }else{
                            out.writeUTF("Usuario Inexistente");
                        }

                        break;
                    }
                    case "Register"-> {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Manda Datos");
                        String user = in.readUTF();
                        String phone = in.readUTF();
                        String password = in.readUTF();
                        int port;
                        if (BuscaRegistro(user,password)){
                            password = EncriptaPSW(password);
                            PreparedStatement query= conexion.prepareStatement("SELECT * FROM users where Phone_Number=? OR username=?;");
                            query.setString(1,phone);
                            query.setString(2,user);
                            ResultSet query_result= query.executeQuery();
                            if(query_result.next()){
                                out.writeUTF("Usuario ya registrado");
                            }else {
                                query = conexion.prepareStatement("SELECT MAX(PORT) FROM users;");
                                query_result = query.executeQuery();
                                if (query_result.next()) {
                                    System.out.println(query_result.getInt(1));
                                    port = (query_result.getInt(1));
                                    port+=1409;
                                } else {
                                    port = 1409;
                                }
                                query = conexion.prepareStatement("INSERT INTO users VALUES (?,?,?,?);");
                                query.setString(1, user);
                                query.setString(2, phone);
                                query.setString(3, password);
                                query.setInt(4, port);
                                query.executeUpdate();
                                query = conexion.prepareStatement("SELECT * FROM users where Phone_Number=? and Username=?;");
                                query.setString(1, phone);
                                query.setString(2, user);
                                query_result = query.executeQuery();
                                if (query_result.next()) {
                                    out.writeUTF("Nuevo Usuario");
                                } else {
                                    out.writeUTF("Algo falló");
                                }
                            }
                        }
                        else{
                            out.writeUTF("El certificado es falso");
                        }
                        break;
                    }
                    case "Busca Contacto"->{
                        DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Manda Datos");
                        String numero=in.readUTF();
                        String nombre=in.readUTF();
                        PreparedStatement query= conexion.prepareStatement("SELECT * from users where Phone_number=? and Username=?");
                        query.setString(1,numero);
                        query.setString(2,nombre);
                        ResultSet query_result=query.executeQuery();
                        if(query_result.next()){
                            out.writeUTF("Usuario Encontrado");
                            String celularUsuario=in.readUTF();
                            int port=query_result.getInt("PORT");
                            System.out.println(port);
                            query=conexion.prepareStatement("SELECT COUNT(*) as total from contacts where (Phone1=? and Phone2=?);");
                            query.setString(1,celularUsuario);
                            query.setString(2,numero);
                            query_result = query.executeQuery();
                            if(query_result.next()){
                                int relacionados=query_result.getInt(1);
                                if(relacionados==0){
                                    query= conexion.prepareStatement("INSERT INTO contacts values (?,?);");
                                    query.setString(1,celularUsuario);
                                    query.setString(2,numero);
                                    query.executeUpdate();
                                    if(!celularUsuario.equals(numero)) {
                                        query = conexion.prepareStatement("INSERT INTO contacts values (?,?);");
                                        query.setString(2, celularUsuario);
                                        query.setString(1, numero);
                                        query.executeUpdate();
                                    }
                                    out.writeUTF("Contacto Agregado");
                                }
                                else{
                                    out.writeUTF("Contacto ya Agregado");
                                }
                            }
                            try{
                              Socket socketC=new Socket("127.0.0.1",port);
                              DataOutputStream outC=new DataOutputStream(socketC.getOutputStream());
                              outC.writeUTF("Nuevo Contacto");
                              socketC.close();
                            }catch (Exception e){

                            }
                        }else{
                            out.writeUTF("Usuario Inexistente");
                        }
                        break;
                    }
                    case "Muestra contactos"->{
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Manda Datos");
                        String userPhone=in.readUTF();
                        List<String> Contactos=new ArrayList<>();
                        PreparedStatement query = conexion.prepareStatement("SELECT users.UserName FROM users,(SELECT contacts.Phone2 as Cel FROM contacts, users where contacts.Phone1 = users.Phone_Number and users.Phone_Number = ?) as First where users.Phone_Number = First.Cel;");
                        query.setString(1,userPhone);
                        ResultSet query_result= query.executeQuery();
                        while (query_result.next()){
                            Contactos.add(query_result.getString("UserName"));
                        }
                        System.out.println(Contactos);
                        ObjectOutputStream oout= new ObjectOutputStream(socket.getOutputStream());
                        oout.writeObject(Contactos);
                        break;
                    }
                    case "Inicia Chat" ->{
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Manda Datos");
                        String contacto = in.readUTF();
                        System.out.println(contacto);
                        PreparedStatement query = conexion.prepareStatement("SELECT *  FROM users where UserName=?;");
                        query.setString(1,contacto);
                        ResultSet query_result = query.executeQuery();
                        if(query_result.next()){
                            System.out.println(query_result.getString("Phone_Number"));
                            System.out.println(query_result.getString("PORT"));
                            out.writeUTF(query_result.getString("Phone_Number"));
                            out.writeInt(Integer.parseInt(query_result.getString("PORT")));
                            out.writeUTF(query_result.getString("Llave_publica"));
                        }
                        break;
                    }
                    case "Manda Mensaje"->{
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Manda Datos");
                        String mensaje = in.readUTF();
                        String emisor = in.readUTF();
                        String receptor = in.readUTF();
                        String seguridad = in.readUTF();
                        String firma = in.readUTF();
                        System.out.println("Bien hecho");
                        if (myController.getInterfere()==true) {
                            mensaje=ModificaMensaje(mensaje);
                        }
                        PreparedStatement query= conexion.prepareStatement("INSERT INTO messages values (?,?,?,NOW(),?,?)");
                        query.setString(1,emisor);
                        query.setString(2,receptor);
                        query.setString(3,mensaje);
                        query.setString(4,seguridad);
                        query.setString(5,firma);
                        query.executeUpdate();
                        System.out.println("hasta aquí");
                        out.writeUTF("Manda Puerto Contacto");
                        int PuertoContacto=in.readInt();
                        System.out.println(PuertoContacto);
                        if(PuertoContacto!=0) {
                            try {
                                Socket sc_contacto = new Socket("127.0.0.1",PuertoContacto);
                                DataOutputStream sc_out = new DataOutputStream(sc_contacto.getOutputStream());
                                sc_out.writeUTF("Nuevo Mensaje");
                            }catch (Exception e){
                                System.out.println("Luego se enterará");
                            }
                        }
                    }
                    case "Consigue Mensajes"->{
                        DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Manda Datos");
                        String Sender=in.readUTF();
                        String Receiver=in.readUTF();
                        List<List<String>> mensajes = new ArrayList<>();
                        PreparedStatement query = conexion.prepareStatement("Select * from messages where (sender=? and reciever = ?) or (sender=? and reciever = ?);");
                        query.setString(1,Sender);
                        query.setString(2,Receiver);
                        query.setString(3,Receiver);
                        query.setString(4,Sender);
                        ResultSet queryResult = query.executeQuery();
                        if(queryResult.next()){
                            List<String> mensaje = new ArrayList<>();
                            mensaje.add(queryResult.getString("sender"));
                            mensaje.add(queryResult.getString("reciever"));
                            mensaje.add(queryResult.getString("message"));
                            mensaje.add(queryResult.getString("Seguridad"));
                            mensaje.add(queryResult.getString("Firma"));
                            System.out.println(mensaje);
                            mensajes.add(mensaje);
                            while (queryResult.next()){
                                mensaje = new ArrayList<>();
                                mensaje.add(queryResult.getString("sender"));
                                mensaje.add(queryResult.getString("reciever"));
                                mensaje.add(queryResult.getString("message"));
                                mensaje.add(queryResult.getString("Seguridad"));
                                mensaje.add(queryResult.getString("Firma"));
                                System.out.println(mensaje);
                                mensajes.add(mensaje);
                            }
                        }
                        else{
                            List<String> mensaje = new ArrayList<>();
                            mensaje.add("System");
                            mensaje.add("System");
                            mensaje.add("No has mandado ningún mensaje");
                            mensaje.add("TP");
                            mensajes.add(mensaje);
                        }
                        ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());

                        oout.writeObject(mensajes);
                        break;
                    }
                    case "Consigue Firma"-> {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Manda datos");
                        String PSW = in.readUTF();
                        PSW=(DesencriptaPSW(PSW));
                        out.writeUTF(PSW);
                        break;
                    }
                    default -> {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("No");
                        System.out.println("Comando no reconocido");

                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean BuscaRegistro(String nombre, String ClavePublica){
        try {
            Socket AR1Socket = new Socket("127.0.0.1", 11111);
            Socket AR2Socket = new Socket("127.0.0.1",22222);
            DataInputStream AR1in= new DataInputStream(AR1Socket.getInputStream());
            DataInputStream AR2in = new DataInputStream(AR2Socket.getInputStream());
            DataOutputStream AR1out = new DataOutputStream(AR1Socket.getOutputStream());
            DataOutputStream AR2out = new DataOutputStream(AR2Socket.getOutputStream());
            AR1out.writeUTF("Busca certificado");
            String respuestaAR1 = AR1in.readUTF();
            if(respuestaAR1.equals("Dame Datos")){
                AR1out.writeUTF(nombre);
                AR1out.writeUTF(ClavePublica);
                String resultado=AR1in.readUTF();
                if(resultado.equals("SI")){
                    AR1Socket.close();
                    AR2Socket.close();
                    return true;
                }
            }
            System.out.println("Aqui no esta");
            AR2out.writeUTF("Busca certificado");
            String respuestaAR2 = AR2in.readUTF();
            if(respuestaAR2.equals("Dame Datos")){
                AR2out.writeUTF(nombre);
                AR2out.writeUTF(ClavePublica);
                String resultado=AR2in.readUTF();
                if(resultado.equals("SI")){
                    AR1Socket.close();
                    AR2Socket.close();
                    return true;
                }
            }
            AR1Socket.close();
            AR2Socket.close();
            return false;
        }catch (Exception e){

        }
        return false;
    }
    private String ModificaMensaje(String mensaje) {
        mensaje=myController.getMessage();
        return mensaje;
    }

    public String EncriptaPSW(String psw){
        char[] char_psw=psw.toCharArray();
        StringBuilder result_psw_Bld = new StringBuilder();
        for(char a : char_psw){
            a+=12;
            result_psw_Bld.append(a);
        }
        return result_psw_Bld.toString();
    }
    public String DesencriptaPSW(String psw){
        char[] char_psw=psw.toCharArray();
        StringBuilder result_psw_Bld = new StringBuilder();
        for(char a : char_psw){
            a-=12;
            if(a<0){
                a+=74;
            }
            result_psw_Bld.append(a);
        }
        return result_psw_Bld.toString();
    }
    public static void setController(ServerController controlador)
    {
        myController = controlador;
    }
}
