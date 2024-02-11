package com.example.whatsup2;

import java.sql.*;


public class BD_Connect {
    private static final String user = "root";
    private static final String password = "";
    private  static  String URL = "jdbc:mysql://localhost:3306/db_whatsup";
    private  static  Connection conn;
    private static BD_Connect instancia;

    private BD_Connect(){

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

    public static BD_Connect GetInstancia(){
        if(instancia==null){
            instancia=new BD_Connect();
        }
        return instancia;
    }
    public void SetDB(String URL){
        this.URL = URL;
    }

}
