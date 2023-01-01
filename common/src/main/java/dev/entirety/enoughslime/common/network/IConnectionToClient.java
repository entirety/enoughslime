package dev.entirety.enoughslime.common.network;

import dev.entirety.enoughslime.common.network.packets.PacketSlime;
import net.minecraft.server.level.ServerPlayer;

public interface IConnectionToClient {

    void sendPacketToClient(PacketSlime packet, ServerPlayer player);

}
