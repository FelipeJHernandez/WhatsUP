package com.example.whatsup2;


public class Cliente {
    private String Nombre;
    private String Celular;
    private int PORT;

    private String LLavePublica;

    public Cliente(String Nombre, String Celular, int PORT, String PK)
    {
        this.Nombre = Nombre;
        this.Celular = Celular;
        this.PORT = PORT;
        this.LLavePublica=PK;
    }

    public String returnName()
    {
        return Nombre;
    }

    public String returnCel(){
        return Celular;
    }
    public int returnPort()
    {
        return PORT;
    }
    public void setName(String Nombre)
    {
        this.Nombre = Nombre;
    }
    public void setPhone(String Celular)
    {
        this.Celular = Celular;
    }
    public void setPort(int PORT)
    {
        this.PORT = PORT;
    }
    public void setPK(String PK)
    {
        this.LLavePublica = PK;
    }

    public String returnPK(){return LLavePublica;}
}
