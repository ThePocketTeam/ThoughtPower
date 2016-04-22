package com.mrgenga.servercobweb;

import com.mrgenga.servercobweb.network.RakNetInterface;

public class Player{

    private RakNetInterface interfaz;
    private long clientID;
    private String address;
    private int port;

    public Player(RakNetInterface interfaz, long clientID, String address, int port){
        this.interfaz = interfaz;
        this.clientID = clientID;
        this.address = address;
        this.port = port;
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
        