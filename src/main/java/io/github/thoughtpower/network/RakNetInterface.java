package io.github.thoughtpower.network;

import io.github.thoughtpower.*;
import io.github.thoughtpower.utils.*;

import cn.nukkit.raknet.RakNet;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.raknet.server.RakNetServer;
import cn.nukkit.raknet.server.ServerHandler;
import cn.nukkit.raknet.server.ServerInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RakNetInterface implements ServerInstance, AdvancedSourceInterface {

    private Server server;

    private RakNetServer raknet;

    private Map<String, Player> players = new ConcurrentHashMap<>();

    private Map<Integer, String> identifiers;

    private Map<String, Integer> identifiersACK = new ConcurrentHashMap<>();

    private ServerHandler handler;

    private int[] channelCounts = new int[256];

    public RakNetInterface(Server server) {
        this.server = server;
        this.identifiers = new ConcurrentHashMap<>();

        this.raknet = new RakNetServer(this.server.getLogger(), this.server.getPort(), this.server.getIp().equals("") ? "0.0.0.0" : this.server.getIp());
        this.handler = new ServerHandler(this.raknet, this);
    }

    @Override
    public boolean process() {
        boolean work = false;
        if (this.handler.handlePacket()) {
            work = true;
            while (this.handler.handlePacket()) {

            }
        }

        this.setName(this.server.getMotd());

        return work;
    }

    @Override
    public void closeSession(String identifier, String reason) {
        if (this.players.containsKey(identifier)) {
            Player player = this.players.get(identifier);
            this.identifiers.remove(player.hashCode());
            this.players.remove(identifier);
            this.identifiersACK.remove(identifier);
            player.close(player.getLeaveMessage(), reason);
        }
    }

    @Override
    public void close(Player player) {
        this.close(player, "unknown reason");
    }

    @Override
    public void close(Player player, String reason) {
        if (this.identifiers.containsKey(player.hashCode())) {
            String id = this.identifiers.get(player.hashCode());
            this.players.remove(id);
            this.identifiersACK.remove(id);
            this.closeSession(id, reason);
            this.identifiers.remove(player.hashCode());
        }
    }

    @Override
    public void shutdown() {
        this.handler.shutdown();
    }

    @Override
    public void emergencyShutdown() {
        this.handler.emergencyShutdown();
    }

    @Override
    public void openSession(String identifier, String address, int port, long clientID) {
        Player player = new Player(this, clientID, address, port);
        this.players.put(identifier, player);
        this.identifiersACK.put(identifier, 0);
        this.identifiers.put(player.hashCode(), identifier);
        this.server.addPlayer(identifier, player);
    }

    @Override
    public void handleEncapsulated(String identifier, EncapsulatedPacket packet, int flags) {
        if (this.players.containsKey(identifier)) {
            try {
                if (packet.buffer.length > 0) {
                    this.players.get(identifier).handleDataPacket(packet.buffer);
                }
            } catch (Exception e) {
                this.server.getLogger().error(e);
                this.server.getLogger().debug("Packet  0x" + Binary.bytesToHexString(packet.buffer));

                if (this.players.containsKey(identifier)) {
                    this.handler.blockAddress(this.players.get(identifier).getAddress(), 5);
                }
            }
        }
    }

    @Override
    public void blockAddress(String address) {
        this.blockAddress(address, 300);
    }

    @Override
    public void blockAddress(String address, int timeout) {
        this.handler.blockAddress(address, timeout);
    }

    @Override
    public void handleRaw(String address, int port, byte[] payload) {
        //this.server.handlePacket(address, port, payload);
    }

    @Override
    public void sendRawPacket(String address, int port, byte[] payload) {
        this.handler.sendRaw(address, port, payload);
    }

    @Override
    public void notifyACK(String identifier, int identifierACK) {

    }

    @Override
    public void setName(String name) {
        this.handler.sendOption("name",
                "MCPE;" + name.replace(";", "\\;") + ";" +
                        ThoughtPower.CURRENT_PROTOCOL + ";" +
                        ThoughtPower.MINECRAFT_VERSION + ";" +
                        this.server.getOnlinePlayers().size() + ";" +
                        this.server.getMaxPlayers());
    }

    public void setPortCheck(boolean value) {
        this.handler.sendOption("portChecking", String.valueOf(value));
    }

    @Override
    public void handleOption(String name, String value) {
        /*if ("bandwidth".equals(name)) {
            String[] v = value.split(";");
            this.network.addStatistics(Double.valueOf(v[0]), Double.valueOf(v[1]));
        }*/
    }

    @Override
    public Integer putPacket(Player player, byte[] buffer) {
        return this.putPacket(player, buffer, false);
    }

    @Override
    public Integer putPacket(Player player, byte[] buffer, boolean needACK) {
        return this.putPacket(player, buffer, needACK, false);
    }

    @Override
    public Integer putPacket(Player player, byte[] buffer, boolean needACK, boolean immediate) {
        if (this.identifiers.containsKey(player.hashCode())) {
            String identifier = this.identifiers.get(player.hashCode());
            EncapsulatedPacket pk = null;
            if (!needACK) {
                pk = new CacheEncapsulatedPacket();
                pk.identifierACK = null;
                pk.buffer = Binary.appendBytes((byte) 0x8e, buffer);
                pk.reliability = 2;
            }

            if (pk == null) {
                pk = new EncapsulatedPacket();
                pk.buffer = Binary.appendBytes((byte) 0x8e, buffer);
                pk.reliability = 2;

                if (needACK) {
                    int iACK = this.identifiersACK.get(identifier);
                    iACK++;
                    pk.identifierACK = iACK;
                    this.identifiersACK.put(identifier, iACK);
                }
            }

            this.handler.sendEncapsulated(identifier, pk, (needACK ? RakNet.FLAG_NEED_ACK : 0) | (immediate ? RakNet.PRIORITY_IMMEDIATE : RakNet.PRIORITY_NORMAL));

            return pk.identifierACK;
        }

        return null;

    }
}
