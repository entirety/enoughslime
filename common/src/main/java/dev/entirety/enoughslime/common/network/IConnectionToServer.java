package dev.entirety.enoughslime.common.network;

import dev.entirety.enoughslime.common.network.packets.PacketSlime;

public interface IConnectionToServer {

    boolean isEnoughSlimeOnServer();

    void sendPacketToServer(PacketSlime packet);

}
