package com.mrgenga.servercobweb.network;

import cn.nukkit.raknet.protocol.*;
import cn.nukkit.raknet.protocol.packet.*;

import java.util.*;

public class StaticPacketPool{
    private static HashMap<Byte, Packet> packetPool;
    private static void registerPacket(byte id, Class<? extends Packet> clazz){
        try{
            StaticPacketPool.packetPool.put(id, clazz.getConstructor().newInstance());
        } catch(Exception e){}
    }

    public static Packet getPacketFromPool(byte id){
        try{
            if(StaticPacketPool.packetPool.isEmpty()) StaticPacketPool.registerPackets();
            if(StaticPacketPool.packetPool.containsKey(id)){
                return StaticPacketPool.packetPool.get(id).clone();
            }
        }catch(Exception e){}

        return null;
    }
    private static void registerPackets(){
        StaticPacketPool.registerPacket(UNCONNECTED_PING.ID, UNCONNECTED_PING.class);
        StaticPacketPool.registerPacket(UNCONNECTED_PING_OPEN_CONNECTIONS.ID, UNCONNECTED_PING_OPEN_CONNECTIONS.class);
        StaticPacketPool.registerPacket(OPEN_CONNECTION_REQUEST_1.ID, OPEN_CONNECTION_REQUEST_1.class);
        StaticPacketPool.registerPacket(OPEN_CONNECTION_REPLY_1.ID, OPEN_CONNECTION_REPLY_1.class);
        StaticPacketPool.registerPacket(OPEN_CONNECTION_REQUEST_2.ID, OPEN_CONNECTION_REQUEST_2.class);
        StaticPacketPool.registerPacket(OPEN_CONNECTION_REPLY_2.ID, OPEN_CONNECTION_REPLY_2.class);
        StaticPacketPool.registerPacket(UNCONNECTED_PONG.ID, UNCONNECTED_PONG.class);
        StaticPacketPool.registerPacket(ADVERTISE_SYSTEM.ID, ADVERTISE_SYSTEM.class);
        StaticPacketPool.registerPacket(DATA_PACKET_0.ID, DATA_PACKET_0.class);
        StaticPacketPool.registerPacket(DATA_PACKET_1.ID, DATA_PACKET_1.class);
        StaticPacketPool.registerPacket(DATA_PACKET_2.ID, DATA_PACKET_2.class);
        StaticPacketPool.registerPacket(DATA_PACKET_3.ID, DATA_PACKET_3.class);
        StaticPacketPool.registerPacket(DATA_PACKET_4.ID, DATA_PACKET_4.class);
        StaticPacketPool.registerPacket(DATA_PACKET_5.ID, DATA_PACKET_5.class);
        StaticPacketPool.registerPacket(DATA_PACKET_6.ID, DATA_PACKET_6.class);
        StaticPacketPool.registerPacket(DATA_PACKET_7.ID, DATA_PACKET_7.class);
        StaticPacketPool.registerPacket(DATA_PACKET_8.ID, DATA_PACKET_8.class);
        StaticPacketPool.registerPacket(DATA_PACKET_9.ID, DATA_PACKET_9.class);
        StaticPacketPool.registerPacket(DATA_PACKET_A.ID, DATA_PACKET_A.class);
        StaticPacketPool.registerPacket(DATA_PACKET_B.ID, DATA_PACKET_B.class);
        StaticPacketPool.registerPacket(DATA_PACKET_C.ID, DATA_PACKET_C.class);
        StaticPacketPool.registerPacket(DATA_PACKET_D.ID, DATA_PACKET_D.class);
        StaticPacketPool.registerPacket(DATA_PACKET_E.ID, DATA_PACKET_E.class);
        StaticPacketPool.registerPacket(DATA_PACKET_F.ID, DATA_PACKET_F.class);
        StaticPacketPool.registerPacket(NACK.ID, NACK.class);
        StaticPacketPool.registerPacket(ACK.ID, ACK.class);
    }
}