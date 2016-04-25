package cn.nukkit.raknet.protocol.packet;

import cn.nukkit.raknet.protocol.Packet;

import java.net.InetSocketAddress;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CLIENT_HANDSHAKE_DataPacket extends Packet {
    public static byte ID = (byte) 0x13;

    @Override
    public byte getID() {
        return ID;
    }

    public String address;
    public int port;
    public InetSocketAddress[] systemAddresses = new InetSocketAddress[]{
            new InetSocketAddress("127.0.0.1", 0),
            new InetSocketAddress("0.0.0.0", 0),
            new InetSocketAddress("0.0.0.0", 0),
            new InetSocketAddress("0.0.0.0", 0),
            new InetSocketAddress("0.0.0.0", 0),
            new InetSocketAddress("0.0.0.0", 0),
            new InetSocketAddress("0.0.0.0", 0),
            new InetSocketAddress("0.0.0.0", 0),
            new InetSocketAddress("0.0.0.0", 0),
            new InetSocketAddress("0.0.0.0", 0)
    };

    public long sendPing;
    public long sendPong;

    @Override
    public void encode() {
        super.encode();
        this.putAddress(new InetSocketAddress(this.address, this.port));

        for(InetSocketAddress a : systemAddresses){
            this.putAddress(a);
        }

        this.putLong(this.sendPing);
        this.putLong(this.sendPong);
    }

    @Override
    public void decode() {
        super.decode();
        InetSocketAddress addr = this.getAddress();
        this.address = addr.getHostString();
        this.port = addr.getPort();

        for (int i = 0; i < 10; i++) {
            this.systemAddresses[i] = this.getAddress();
        }

        this.sendPing = this.getLong();
        this.sendPong = this.getLong();
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new CLIENT_HANDSHAKE_DataPacket();
        }

    }

}
