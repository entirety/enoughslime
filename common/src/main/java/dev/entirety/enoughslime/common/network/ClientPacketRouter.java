package dev.entirety.enoughslime.common.network;

import dev.entirety.enoughslime.common.network.packets.IClientPacketHandler;
import dev.entirety.enoughslime.common.network.packets.client.PacketUpdateRecipes;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Optional;

public class ClientPacketRouter {

    private static final Logger LOGGER = LogManager.getLogger();

    public final EnumMap<ClientPacketId, IClientPacketHandler> clientHandlers = new EnumMap<>(ClientPacketId.class);

    private final IConnectionToServer connection;

    public ClientPacketRouter(IConnectionToServer connection) {
        this.connection = connection;

        clientHandlers.put(ClientPacketId.UPDATE_RECIPES, PacketUpdateRecipes::readPacketData);
    }

    public void onPacket(FriendlyByteBuf packetBuffer, LocalPlayer player) {
        getPacketId(packetBuffer)
            .ifPresent(packetId -> {
                IClientPacketHandler packetHandler = clientHandlers.get(packetId);
                ClientPacketContext context = new ClientPacketContext(player, connection);
                ClientPacketData data = new ClientPacketData(packetBuffer, context);

                try {
                    packetHandler.readPacketData(data)
                        .exceptionally(throwable -> {
                            LOGGER.error("Packet error while executing packet on the client thread: {}", packetId.name(), throwable);
                            return null;
                        });
                } catch (Throwable throwable) {
                    LOGGER.error("Packet error when reading packet: {}", packetId.name(), throwable);
                }
            });
    }

    private Optional<ClientPacketId> getPacketId(FriendlyByteBuf packetBuffer) {
        if (packetBuffer.readableBytes() < 1) {
            LOGGER.warn("Received empty packet");
            return Optional.empty();
        }

        int packetId = packetBuffer.readByte();
        if (packetId < 0 || packetId >= ClientPacketId.VALUES.length) {
            LOGGER.warn("Received invalid packet id: {}", packetId);
            return Optional.empty();
        }

        return Optional.of(ClientPacketId.VALUES[packetId]);
    }

}
