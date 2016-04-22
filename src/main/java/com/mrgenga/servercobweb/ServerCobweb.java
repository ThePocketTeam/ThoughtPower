package com.mrgenga.servercobweb;

public class ServerCobweb implements Runnable {

    public final static Integer CURRENT_PROTOCOL = 45;
    public final static String MINECRAFT_VERSION = "0.14.0";

    public static void main(String[] args){
        (new ServerCobweb()).run();
    }

    public ServerCobweb(){
    }

   @Override
    public void run(){
        Server server = new Server();
        server.run();
    }

}