package dev.entirety.enoughslime.common.network;

import net.minecraft.client.player.LocalPlayer;

public record ClientPacketContext(LocalPlayer player, IConnectionToServer connection) {

}
