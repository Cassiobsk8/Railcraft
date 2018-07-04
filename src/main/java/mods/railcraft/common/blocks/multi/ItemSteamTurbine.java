package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemSteamTurbine extends ItemMultiBlock {

    public ItemSteamTurbine(Block block) {
        super(block);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, IBlockState state) {
        return new ModelResourceLocation(block.getRegistryName(), "inventory");
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addRecipe(stack,
                "BPB",
                "P P",
                "BPB",
                'B', "blockSteel",
                'P', "plateSteel");
    }
}
