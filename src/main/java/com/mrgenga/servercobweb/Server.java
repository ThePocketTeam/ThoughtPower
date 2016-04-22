package com.mrgenga.servercobweb;

import com.mrgenga.servercobweb.network.RakNetInterface;

import org.apache.logging.log4j.*;
import org.yaml.snakeyaml.Yaml;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable{

    private static Server instance;
    public static Server getInstance(){
        return instance;
    }

    private Logger logger;
    private RakNetInterface interfaz;
    private Map<String, Object> config;
    private Yaml yml;

    private Map<String, Player> players = new HashMap<String, Player>();

    public Logger getLogger(){
        return this.logger;
    }

    public RakNetInterface getInterface(){
        return interfaz;
    }

    public Map<String, Object> getConfig(){
        return config;
   }

    public Yaml getYaml(){
        return this.yml;
    }

    public String getIp(){
        return (String) config.get("bindAddress");
    }

    public Integer getPort(){
        return (Integer) config.get("bindPort");
    }

    public Integer getMaxPlayers(){
        return (Integer) config.get("maxPlayersCount");
    }

    public Map<String, Player> getOnlinePlayers(){
        return this.players;
    }

    public void addPlayer(String identifier, Player player){
        this.players.put(identifier, player);
    }

    public void removePlayer(Player player){
        this.players.values().remove(player);
    }

    public String getMotd(){
        return (String) config.get("motd");
    }

    public void setMotd(String motd){
        config.put("motd", motd);
        interfaz.setName(motd);
    }

    private void checkConfig() throws IOException { 
        File configFile = new File("config.yml"); 
        if(!configFile.exists()){ 
            InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml"); 
            BufferedReader reader = new BufferedReader(new InputStreamReader(in)); 
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile)); 
            String line = ""; 
            while((line = reader.readLine()) != null){ 
                writer.write(line+"\n"); 
            } 
           reader.close(); 
           writer.close(); 
        } 
    }

    public Server(){
        instance = this;
        logger = LogManager.getLogger("ServerCobweb");
        yml = new Yaml();
        try{
            checkConfig();
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            config = (Map<String, Object>) yml.load(new FileReader("config.yml"));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        Thread.currentThread().setName("ServerCobweb");
        interfaz = new RakNetInterface(this);
        logger.info("Server started!");
        while(true) interfaz.process();
    }

}