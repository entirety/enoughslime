package dev.entirety.enoughslime.common.network.packets.client;

import dev.entirety.enoughslime.common.network.ClientPacketContext;
import dev.entirety.enoughslime.common.network.ClientPacketData;
import dev.entirety.enoughslime.common.network.ClientPacketId;
import dev.entirety.enoughslime.common.network.IPacketId;
import dev.entirety.enoughslime.common.network.packets.PacketSlime;
import dev.entirety.enoughslime.common.slimefun.items.ItemGroup;
import dev.entirety.enoughslime.common.slimefun.items.SlimefunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PacketUpdateRecipes extends PacketSlime {

    private final List<ItemGroup> itemGroups;
    private final List<SlimefunItem> items;

    public PacketUpdateRecipes(List<ItemGroup> itemGroups, List<SlimefunItem> items) {
        this.itemGroups = itemGroups;
        this.items = items;
    }

    @Override
    protected IPacketId getPacketId() {
        return ClientPacketId.UPDATE_RECIPES;
    }

    @Override
    protected void writePacketData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(itemGroups.size());
        for (ItemGroup itemGroup : itemGroups) {
            CompoundTag tag = new CompoundTag();

            itemGroup.write(tag);
            buffer.writeNbt(tag);
        }

        buffer.writeVarInt(items.size());
        for (SlimefunItem item : items) {
            CompoundTag tag = new CompoundTag();

            item.write(tag);
            buffer.writeNbt(tag);
        }
    }

    public static CompletableFuture<Void> readPacketData(ClientPacketData data) {
        Minecraft minecraft = Minecraft.getInstance();
        FriendlyByteBuf buffer = data.buf();
        ClientPacketContext context = data.context();

        int itemGroupCount = buffer.readVarInt();
        List<ItemGroup> itemGroups = new ArrayList<>();
        for (int i = 0; i < itemGroupCount; i++) {
            CompoundTag tag = buffer.readNbt();
            if (tag == null) continue;

            itemGroups.add(ItemGroup.of(tag));
        }

        int itemCount = buffer.readVarInt();
        List<SlimefunItem> items = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            CompoundTag tag = buffer.readNbt();
            if (tag == null) continue;

            items.add(SlimefunItem.of(tag));
        }

        return minecraft.submit(() -> UpdateRecipesHandler.handleUpdateRecipes(context, itemGroups, items));
    }

}
