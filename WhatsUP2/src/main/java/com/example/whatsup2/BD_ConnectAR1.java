package com.example.whatsup2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class BD_ConnectAR1 {
    private static final String user = "root";
    private static final String password = "";
    private  static  String URL = "jdbc:mysql://localhost:3306/ar1";
    private  static  Connection conn;
    private static BD_ConnectAR1 instancia;

    private BD_ConnectAR1(){

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

    public static BD_ConnectAR1 GetInstancia(){
        if(instancia==null){
            instancia=new BD_ConnectAR1();
        }
        return instancia;
    }

}
