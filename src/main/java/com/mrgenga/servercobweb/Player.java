package com.mrgenga.servercobweb;

import com.mrgenga.servercobweb.network.*;

public class Player{

    private RakNetInterface interfaz;
    private RakNetConnection connection;
    private long clientID;
    private String address;
    private int port;
    private Server server;

    public Player(RakNetInterface interfaz, long clientID, String address, int port){
        this.interfaz = interfaz;
        this.clientID = clientID;
        this.address = address;
        this.port = port;
        this.server = Server.getInstance();
        this.connection = new RakNetConnection(this, (String)this.server.getConfig().get("remoteAddress"), (Integer)this.server.getConfig().get("remotePort"));
    }

    public RakNetConnection getConnection(){
        return this.connection;
    }

    public Server getServer(){
        return this.server;
    }

    public long getClientId(){
        return clientID;
    }

    public String getAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }

    public String getLeaveMessage(){
        return "";
    }

    public void close(){
        this.close("");
    }

    public void close(String message){
        this.close(message, message);
    }

    public void close(String message, String reason){
        //TODO
    }

    public void handleDataPacket(byte[] buffer){
        //TODO
    }
}
        