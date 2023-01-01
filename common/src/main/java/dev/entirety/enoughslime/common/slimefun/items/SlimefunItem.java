package dev.entirety.enoughslime.common.slimefun.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public record SlimefunItem(
    String id,
    ItemStack itemStackTemplate,
    ItemState state,
    String itemGroup,
    ItemStack[] recipe,
    String recipeType,
    ItemStack recipeOutput
) {

    public void write(CompoundTag tag) {
        tag.putString("id", id);
        tag.put("itemStackTemplate", itemStackTemplate.save(new CompoundTag()));
        tag.putString("state", state.name());

        ListTag recipe = new ListTag();
        for (ItemStack input : this.recipe) {
            recipe.add(input.save(new CompoundTag()));
        }

        tag.putString("itemGroup", itemGroup);
        tag.put("recipe", recipe);
        tag.putString("recipeType", recipeType);
        tag.put("recipeOutput", recipeOutput.save(new CompoundTag()));
    }

    public static SlimefunItem of(CompoundTag tag) {
        String id = tag.getString("id");
        ItemStack itemStackTemplate = ItemStack.of(tag.getCompound("itemStackTemplate"));
        ItemState state = ItemState.valueOf(tag.getString("state"));
        String itemGroup = tag.getString("itemGroup");

        ListTag recipe = tag.getList("recipe", 10);
        ItemStack[] recipeInputs = new ItemStack[recipe.size()];
        for (int i = 0; i < recipe.size(); i++) {
            recipeInputs[i] = ItemStack.of(recipe.getCompound(i));
        }

        String recipeType = tag.getString("recipeType");
        ItemStack recipeOutput = ItemStack.of(tag.getCompound("recipeOutput"));

        return new SlimefunItem(id, itemStackTemplate, state, itemGroup, recipeInputs, recipeType, recipeOutput);
    }

}
