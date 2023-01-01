package dev.entirety.enoughslime.common.network;

import net.minecraft.network.FriendlyByteBuf;

public record ClientPacketData(FriendlyByteBuf buf, ClientPacketContext context) {

}
