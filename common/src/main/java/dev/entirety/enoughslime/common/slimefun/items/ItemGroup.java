package dev.entirety.enoughslime.common.slimefun.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public record ItemGroup(String key, ItemStack item) {

    public void write(CompoundTag tag) {
        tag.putString("id", key);
        tag.put("item", item.save(new CompoundTag()));
    }

    public static ItemGroup of(CompoundTag tag) {
        String key = tag.getString("id");
        ItemStack item = ItemStack.of(tag.getCompound("item"));

        return new ItemGroup(key, item);
    }

}
