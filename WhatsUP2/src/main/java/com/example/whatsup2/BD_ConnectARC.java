package com.example.whatsup2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class BD_ConnectARC {
    private static final String user = "root";
    private static final String password = "";
    private  static  String URL = "jdbc:mysql://localhost:3306/arc";
    private  static  Connection conn;
    private static BD_ConnectARC instancia;

    private BD_ConnectARC(){

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

    public static BD_ConnectARC GetInstancia(){
        if(instancia==null){
            instancia=new BD_ConnectARC();
        }
        return instancia;
    }

}
