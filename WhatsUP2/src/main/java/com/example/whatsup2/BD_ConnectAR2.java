package com.example.whatsup2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class BD_ConnectAR2 {
    private static final String user = "root";
    private static final String password = "";
    private  static  String URL = "jdbc:mysql://localhost:3306/ar2";
    private  static  Connection conn;
    private static BD_ConnectAR2 instancia;

    private BD_ConnectAR2(){

    }
    public  Connection CreateConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL,user,password);
            return  conn;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void EndConnection() throws SQLException{
        try{
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
            conn.close();
        }finally {
            conn.close();
        }
    }

    public static BD_ConnectAR2 GetInstancia(){
        if(instancia==null){
            instancia=new BD_ConnectAR2();
        }
        return instancia;
    }

}
