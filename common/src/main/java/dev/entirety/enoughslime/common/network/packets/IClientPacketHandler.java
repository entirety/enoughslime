package dev.entirety.enoughslime.common.network.packets;

import dev.entirety.enoughslime.common.network.ClientPacketData;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface IClientPacketHandler {

    CompletableFuture<Void> readPacketData(ClientPacketData data);

}
