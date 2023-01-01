package dev.entirety.enoughslime.common.network.packets.client;

import dev.entirety.enoughslime.common.network.ClientPacketContext;
import dev.entirety.enoughslime.common.slimefun.items.ItemGroup;
import dev.entirety.enoughslime.common.slimefun.items.SlimefunItem;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class UpdateRecipesHandler {

    public static void handleUpdateRecipes(ClientPacketContext context, List<ItemGroup> itemGroups, List<SlimefunItem> items) {
        LogManager.getLogger().info("===================[ Received update recipes packet: (" + itemGroups.size() + " - " + items.size() + ") ]===================");
    }

}
