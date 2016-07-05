package io.github.thoughtpower;

public class ThoughtPower implements Runnable {

    public final static Integer CURRENT_PROTOCOL = 81;
    public final static String MINECRAFT_VERSION = "0.15.1";

    public static void main(String[] args){
        (new ThoughtPower()).run();
    }

    public ThoughtPower(){
    }

   @Override
    public void run(){
        Server server = new Server();
        server.run();
    }

}
