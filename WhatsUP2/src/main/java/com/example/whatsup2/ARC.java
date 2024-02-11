package com.example.whatsup2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.*;

public class ARC implements  Runnable{
    public static BD_ConnectARC con = BD_ConnectARC.GetInstancia();

    ServerSocket servidor;
    public void run(){
        try {
            servidor = new ServerSocket(42000);
            Connection conexion = con.CreateConnection();
            while (true){
                Socket socket = servidor.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String comando=in.readUTF();
                if(comando.equals("Busca llave")){
                    out.writeUTF("Manda llave");
                    String llave=in.readUTF();
                    PreparedStatement query = conexion.prepareStatement("SELECT COUNT(*) as total from clavespublicas where Clave = ?;");
                    query.setString(1,llave);
                    ResultSet query_result=query.executeQuery();
                    if(query_result.next()){
                        int cuenta = query_result.getInt("total");
                        if(cuenta!=0){
                            out.writeUTF("Llave ocupada");
                            System.out.println("Todo bien");
                        }
                        else{
                            out.writeUTF("No existe");
                        }
                    }
                }
                if(comando.equals("Dame numcert")){
                    out.writeUTF("Manda clave");
                    String RegistraClave = in.readUTF();
                    PreparedStatement query = conexion.prepareStatement("INSERT INTO clavespublicas (clave) VALUES (?);");
                    query.setInt(1,Integer.parseInt(RegistraClave));
                    query.executeUpdate();
                    query = conexion.prepareStatement("SELECT IDCertificado as numcert from clavespublicas where clave=?;");
                    query.setInt(1,Integer.parseInt(RegistraClave));
                    ResultSet query_result = query.executeQuery();
                    if(query_result.next()){
                        out.writeUTF(query_result.getString("numcert"));
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
