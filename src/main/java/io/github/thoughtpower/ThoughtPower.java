package io.github.thoughtpower;

public class ThoughtPower implements Runnable {

    public final static Integer CURRENT_PROTOCOL = 45;
    public final static String MINECRAFT_VERSION = "0.14.0";

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
