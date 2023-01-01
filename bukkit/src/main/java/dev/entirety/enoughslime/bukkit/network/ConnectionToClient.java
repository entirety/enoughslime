package dev.entirety.enoughslime.bukkit.network;

import dev.entirety.enoughslime.common.Constants;
import dev.entirety.enoughslime.common.network.IConnectionToClient;
import dev.entirety.enoughslime.common.network.packets.PacketSlime;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;

public class ConnectionToClient implements IConnectionToClient {

    @Override
    public void sendPacketToClient(PacketSlime packet, ServerPlayer player) {
        ClientboundCustomPayloadPacket payload = new ClientboundCustomPayloadPacket(Constants.NETWORK_CHANNEL_ID, packet.getPacketData().getLeft());
        player.connection.send(payload);
    }

}
