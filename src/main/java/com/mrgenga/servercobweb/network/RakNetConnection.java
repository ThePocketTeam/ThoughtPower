package com.mrgenga.servercobweb.network;

import cn.nukkit.raknet.protocol.*;
import cn.nukkit.raknet.protocol.packet.*;
import cn.nukkit.raknet.server.UDPServerSocket;
import java.nio.channels.DatagramChannel;
import java.net.*;
import java.util.*;
import com.mrgenga.servercobweb.*;

public class RakNetConnection extends UDPServerSocket{
    public final static Integer START_PORT = 49256;
    private static Integer instanceId = 0;

    private boolean isConnected;

    private Player player;
    private String ip;
    private Integer port;

    private String name;

    private Integer sequenceNumber;
    private TreeMap<Integer, Integer> ackQueue;

    private long lastSendTime;
    private long pingCount;

    public RakNetConnection(Player player, String ip, Integer port){
        super(player.getServer().getLogger(), RakNetConnection.START_PORT + RakNetConnection.instanceId, player.getServer().getIp());
        try{
            this.socket.setReuseAddress(false); 
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        RakNetConnection.instanceId += 1;

        this.player = player;
        this.ip = ip;
        this.port = port;
        this.name = "";
        this.sequenceNumber = 0;
        this.ackQueue = new TreeMap<Integer, Integer>();
        this.isConnected = false;
        this.lastSendTime = -1;
        this.pingCount = 0;
    }

    public void finalize(){
        RakNetConnection.instanceId -= 1; 
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int sendPacket(Packet packet){
        try{
            this.lastSendTime = (Long)System.currentTimeMillis() / 1000;
            packet.encode();
            return this.writePacket(packet.buffer, this.ip, this.port);
        } catch(Exception e) { 
            e.printStackTrace(); 
            return 0;
        }
    }

    public int sendEncapsulatedPacket(Packet packet){
        packet.encode();
        EncapsulatedPacket encapsulated = new EncapsulatedPacket();
        encapsulated.reliability = 0;
        encapsulated.buffer = packet.buffer;

        DATA_PACKET_4 sendPacket = new DATA_PACKET_4();
        sendPacket.seqNumber = this.sequenceNumber++;
        sendPacket.sendTime = System.currentTimeMillis();
        sendPacket.packets.add(encapsulated.toBinary());

        return this.sendPacket(sendPacket);
    }

    public byte[] readPacket(String ip, Integer port){
        try{
            DatagramPacket pk = this.readPacket();
            if(pk == null) return null;
            if(!(pk.getAddress().equals(InetAddress.getByName(ip)) && pk.getPort() == port)) return null;
            return pk.getData();
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Packet receivePacket(){
        byte[] buffer = this.readPacket(this.ip, this.port);
        if (buffer != null) {
            try{
                Packet packet = new BufferPacket();
                if ((packet = StaticPacketPool.getPacketFromPool(buffer[0])) != null) {
                    packet.buffer = buffer;
                    packet.decode();
                    if (packet instanceof DataPacket) {
                        DataPacket pk = (DataPacket) packet;
                        this.ackQueue.put(pk.seqNumber, pk.seqNumber);
                    }
                    return packet;
                }
                packet.buffer = buffer;
                return packet;
            } catch(NullPointerException e){ return null; }
        }
        else{
            return null;
        }
    }
    public void tick(){
        if(this.lastSendTime != ((Long)System.currentTimeMillis() / 1000)){
            if(this.isConnected()){
                PING_DataPacket ping = new PING_DataPacket();
                ping.pingID = this.pingCount++;
                this.sendEncapsulatedPacket(ping);
            } else {
                UNCONNECTED_PING ping = new UNCONNECTED_PING();
                ping.pingID = this.pingCount++;
                this.sendPacket(ping);
            }
        }
        if(this.ackQueue.size() > 0 && this.lastSendTime != ((Long)System.currentTimeMillis() / 1000)){
            ACK ack = new ACK();
            ack.packets = this.ackQueue;
            this.sendPacket(ack);
            this.ackQueue.clear();
        }
        Packet pk;
        if((pk = this.receivePacket()) instanceof Packet){
            if(pk instanceof DataPacket){
                DataPacket dpk = (DataPacket) pk;
                for(Object obj : dpk.packets){
                    EncapsulatedPacket epk = (EncapsulatedPacket) obj;
                    byte id = epk.buffer[0];
                    if(SERVER_HANDSHAKE_DataPacket.ID == id){
                        SERVER_HANDSHAKE_DataPacket npk = new SERVER_HANDSHAKE_DataPacket();
                        npk.buffer = epk.buffer;
                        npk.decode();
                        this.handlePacket(npk);
                    }
                    else if(PONG_DataPacket.ID == id){
                        PONG_DataPacket npk = new PONG_DataPacket();
                        npk.buffer = epk.buffer;
                        npk.decode();
                        this.handlePacket(npk);
                    }
                    else {
                        this.handleDataPacket(epk.buffer);
                    }
                }
            }
            else {
                this.handlePacket(pk);
            }
        }
    }

    public Player getPlayer(){
        return this.player;
    }

    public String getIp(){
        return this.ip;
    }

    public Integer getPort(){
        return this.port;
    }

    public boolean isConnected(){
        return this.isConnected;
    }

    public void setIsConnected(boolean isConnected){
        this.isConnected = isConnected;
    }

    public void handlePacket(Packet packet){
        if(packet.getID() == UNCONNECTED_PONG.ID){
            UNCONNECTED_PONG up = (UNCONNECTED_PONG) packet;
            this.setName(up.serverName);
            this.setIsConnected(true);
            OPEN_CONNECTION_REQUEST_1 pk = new OPEN_CONNECTION_REQUEST_1();
            pk.mtuSize = 1447;
            this.sendPacket(pk);
        }
        else if(packet.getID() == OPEN_CONNECTION_REPLY_1.ID){
            OPEN_CONNECTION_REPLY_1 up1 = (OPEN_CONNECTION_REPLY_1) packet;
            OPEN_CONNECTION_REQUEST_2 pk1 = new OPEN_CONNECTION_REQUEST_2();
            pk1.serverPort = this.getPort();
            pk1.mtuSize = up1.mtuSize;
            pk1.clientID = this.player.getClientId();
            this.sendPacket(pk1);
        }
        else if(packet.getID() == OPEN_CONNECTION_REPLY_2.ID){
            CLIENT_CONNECT_DataPacket pk2 = new CLIENT_CONNECT_DataPacket();
            pk2.clientID = this.player.getClientId();
            pk2.sendPing = System.currentTimeMillis();
            this.sendEncapsulatedPacket(pk2);
        }
        else if(packet.getID() == SERVER_HANDSHAKE_DataPacket.ID){
            CLIENT_HANDSHAKE_DataPacket pk3 = new CLIENT_HANDSHAKE_DataPacket();
            pk3.address = this.getIp();
            pk3.port = this.getPort();
            pk3.sendPing = System.currentTimeMillis();
            pk3.sendPong = pk3.sendPing + 1000L;
            this.sendEncapsulatedPacket(pk3);

            //TODO: Login

            PING_DataPacket pk4 = new PING_DataPacket();
            pk4.pingID = this.pingCount++;
            this.sendEncapsulatedPacket(pk4);
        }
    }

    public void handleDataPacket(byte[] buffer){
        //TODO
    }

}