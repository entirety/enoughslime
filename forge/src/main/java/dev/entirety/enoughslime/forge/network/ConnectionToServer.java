package dev.entirety.enoughslime.forge.network;

import com.google.common.collect.ImmutableMap;
import dev.entirety.enoughslime.common.network.IConnectionToServer;
import dev.entirety.enoughslime.common.network.packets.PacketSlime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.UUID;

public class ConnectionToServer implements IConnectionToServer {

    private static UUID enoughSlimeCacheId = null;
    private static boolean enoughSlimeCacheEnabled = false;

    private final NetworkHandler networkHandler;

    public ConnectionToServer(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    @Override
    public boolean isEnoughSlimeOnServer() {
        Minecraft minecraft = Minecraft.getInstance();

        ClientPacketListener clientPacketListener = minecraft.getConnection();
        if (clientPacketListener == null) return false;

        UUID id = clientPacketListener.getId();
        if (!id.equals(enoughSlimeCacheId)) {
            enoughSlimeCacheId = id;
            enoughSlimeCacheEnabled = Optional.of(clientPacketListener)
                .map(ClientPacketListener::getConnection)
                .map(NetworkHooks::getConnectionData)
                .map(ConnectionData::getChannels)
                .map(ImmutableMap::keySet)
                .map(keys -> keys.contains(networkHandler.getChannelId()))
                .orElse(false);
        }

        return enoughSlimeCacheEnabled;
    }

    @Override
    public void sendPacketToServer(PacketSlime packet) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener netHandler = minecraft.getConnection();

        if (netHandler != null && isEnoughSlimeOnServer()) {
            Pair<FriendlyByteBuf, Integer> packetData = packet.getPacketData();
            ICustomPacket<Packet<?>> payload = NetworkDirection.PLAY_TO_SERVER.buildPacket(packetData, networkHandler.getChannelId());
            netHandler.send(payload.getThis());
        }
    }

}
