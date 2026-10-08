package com.direwolf20.justdirethings.common.blocks.gooblocks;

import com.direwolf20.justdirethings.common.blockentities.gooblocks.GooBlockBE_Tier4;
import com.direwolf20.justdirethings.datagen.JustDireItemTags;
import com.direwolf20.justdirethings.setup.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class GooBlock_Tier4 extends GooBlock_Base implements EntityBlock {
	public GooBlock_Tier4() {
		super();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GooBlockBE_Tier4(pos, state);
	}

	/**
	 * Tags can't read config, so use_sculk_recipes picks the items here. The renderer shows the same ones.
	 */
	public static List<Item> revivalItems() {
		if (Config.USE_SCULK_RECIPES.get())
			return ForgeRegistries.ITEMS.tags().getTag(JustDireItemTags.GOO_REVIVE_TIER_4).stream().toList();
		return List.of(Items.ENDER_EYE);
	}

	@Override
	protected boolean validRevivalItem(ItemStack itemStack) {
		return revivalItems().contains(itemStack.getItem());
	}

}
