package dev.entirety.enoughslime.bukkit;

import dev.entirety.enoughslime.bukkit.network.ConnectionToClient;
import dev.entirety.enoughslime.common.Constants;
import dev.entirety.enoughslime.common.Internal;
import dev.entirety.enoughslime.common.network.packets.PacketSlime;
import dev.entirety.enoughslime.common.network.packets.client.PacketUpdateRecipes;
import dev.entirety.enoughslime.common.slimefun.items.ItemGroup;
import dev.entirety.enoughslime.common.slimefun.items.ItemState;
import dev.entirety.enoughslime.common.slimefun.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class EnoughSlime extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.NETWORK_CHANNEL_ID.toString());
        getServer().getPluginManager().registerEvents(this, this);

        Internal.setClientConnection(new ConnectionToClient());
    }

    @Override
    public void onDisable() {
        Internal.setClientConnection(null);

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        List<ItemGroup> itemGroups = Slimefun.getRegistry().getAllItemGroups().stream()
            .map(itemGroup -> new ItemGroup(itemGroup.getKey().toString(), CraftItemStack.asNMSCopy(itemGroup.getItem(event.getPlayer())))).toList();

        List<SlimefunItem> items = Slimefun.getRegistry().getAllSlimefunItems().stream()
            .map(item -> {
                String id = item.getId();
                ItemStack itemStackTemplate = CraftItemStack.asNMSCopy(item.getItem());
                ItemState state = ItemState.valueOf(item.getState().name());
                String itemGroup = item.getItemGroup().getKey().toString();
                ItemStack[] recipe = Arrays.stream(item.getRecipe()).map(CraftItemStack::asNMSCopy).toArray(ItemStack[]::new);
                String recipeType = item.getRecipeType().getKey().toString();
                ItemStack recipeOutput = CraftItemStack.asNMSCopy(item.getRecipeOutput());

                return new SlimefunItem(id, itemStackTemplate, state, itemGroup, recipe, recipeType, recipeOutput);
            }).toList();

        PacketSlime packet = new PacketUpdateRecipes(itemGroups, items);
        ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();

        Internal.getClientConnection().sendPacketToClient(packet, player);
    }

}
