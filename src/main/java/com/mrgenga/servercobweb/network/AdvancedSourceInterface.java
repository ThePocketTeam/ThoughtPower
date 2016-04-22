package com.mrgenga.servercobweb.network;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface AdvancedSourceInterface extends SourceInterface {

    void blockAddress(String address);

    void blockAddress(String address, int timeout);

    void sendRawPacket(String address, int port, byte[] payload);
}
