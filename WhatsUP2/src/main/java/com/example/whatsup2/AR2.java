package com.example.whatsup2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AR2 implements  Runnable{
    public  static BD_ConnectAR2 con = BD_ConnectAR2.GetInstancia();

    ServerSocket server;
    public void run(){
        try{
            server =new ServerSocket(22222);
            Connection conexion = con.CreateConnection();
            while (true){
                Socket socket = server.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String comando=in.readUTF();
                if(comando.equals("Busca una llave")){
                    out.writeUTF("Dame la llave");
                    String llave = in.readUTF();
                    Socket ARCSocket = new Socket("127.0.0.1",42000);
                    DataInputStream ARCin = new DataInputStream(ARCSocket.getInputStream());
                    DataOutputStream ARCout = new DataOutputStream(ARCSocket.getOutputStream());
                    ARCout.writeUTF("Busca llave");
                    String respuesta = ARCin.readUTF();
                    if(respuesta.equals("Manda llave")){
                        ARCout.writeUTF(llave);
                        respuesta=ARCin.readUTF();
                        if(respuesta.equals("No existe")){
                            out.writeUTF("Llave libre");
                        }
                        else{
                            out.writeUTF("Llave ocupada");
                        }
                    }
                }
                if(comando.equals("Dame un certificado")){
                    out.writeUTF("Manda Datos");
                    String Nombre = in.readUTF();
                    String Domicilio = in.readUTF();
                    String Frase = in.readUTF();
                    String ClavePublica = in.readUTF();
                    Socket ARCSocket = new Socket("127.0.0.1",42000);
                    DataInputStream ARCin = new DataInputStream(ARCSocket.getInputStream());
                    DataOutputStream ARCout = new DataOutputStream(ARCSocket.getOutputStream());
                    PreparedStatement query=conexion.prepareStatement("SELECT * FROM certificados where Duenho=? and Domicilio=?");
                    query.setString(1,Nombre);
                    query.setString(2,Domicilio);
                    ResultSet query_result=query.executeQuery();
                    if(!query_result.next()) {
                        ARCout.writeUTF("Dame numcert");
                        String respuesta = ARCin.readUTF();
                        if(respuesta.equals("Manda clave")){
                            ARCout.writeUTF(ClavePublica);
                            String numcert = ARCin.readUTF();
                            query = conexion.prepareStatement("INSERT INTO certificados values (?,?,?,?,CURRENT_DATE, DATE_ADD(CURRENT_DATE,INTERVAL 4 YEAR));");
                            query.setInt(1, Integer.parseInt(numcert));
                            query.setString(2, Nombre);
                            query.setInt(3, Integer.parseInt(ClavePublica));
                            query.setString(4, Domicilio);
                            query.executeUpdate();
                            out.writeUTF("Certificado registrado");
                            out.writeUTF(numcert);
                            query = conexion.prepareStatement("SELECT Fecha_Emision,Fecha_Vencimiento from certificados where IDCertificado=?;");
                            query.setInt(1, Integer.parseInt(numcert));
                            ResultSet query_result2 = query.executeQuery();
                            if (query_result2.next()) {
                                out.writeUTF(query_result2.getString("Fecha_Emision"));
                                out.writeUTF(query_result2.getString("Fecha_Vencimiento"));
                            }
                        }
                    }
                    else{
                        out.writeUTF("N0");
                    }
                }
                if (comando.equals("Busca certificado")){
                    out.writeUTF("Dame Datos");
                    String nombre=in.readUTF();
                    String Llave=in.readUTF();
                    PreparedStatement query=conexion.prepareStatement("SELECT * from certificados where Llave=? and Duenho=?");
                    query.setInt(1,Integer.valueOf(Llave));
                    query.setString(2,nombre);
                    ResultSet query_result=query.executeQuery();
                    if (query_result.next()){
                        out.writeUTF("SI");
                    }
                    else {
                        out.writeUTF("NO");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
