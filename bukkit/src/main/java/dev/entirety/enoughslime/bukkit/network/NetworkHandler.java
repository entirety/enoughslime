package dev.entirety.enoughslime.bukkit.network;

import dev.entirety.enoughslime.bukkit.EnoughSlime;
import net.minecraft.resources.ResourceLocation;

public class NetworkHandler {

    private final EnoughSlime plugin;
    private final ResourceLocation channelId;

    public NetworkHandler(EnoughSlime plugin, ResourceLocation channelId) {
        this.plugin = plugin;
        this.channelId = channelId;

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelId.toString());

        registerPacketFilter();
    }

    public void registerPacketFilter() {

    }

}
