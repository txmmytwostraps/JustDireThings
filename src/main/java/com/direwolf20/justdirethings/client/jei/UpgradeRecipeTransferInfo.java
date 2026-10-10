package com.direwolf20.justdirethings.client.jei;

import com.direwolf20.justdirethings.common.blockentities.DireSmithingTableBE;
import com.direwolf20.justdirethings.common.containers.DireSmithingTableContainer;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.UpgradeRecipe;

import java.util.List;

/**
 * Moves vanilla smithing recipes into the Dire Smithing Table. The template slot goes last so JEI clears it.
 * A leftover template blocks vanilla recipes.
 */
public class UpgradeRecipeTransferInfo implements IRecipeTransferInfo<DireSmithingTableContainer, UpgradeRecipe> {
	@Override
	public Class<DireSmithingTableContainer> getContainerClass() {
		return DireSmithingTableContainer.class;
	}

	@Override
	public RecipeType<UpgradeRecipe> getRecipeType() {
		return RecipeTypes.SMITHING;
	}

	@SuppressWarnings("removal")
	@Override
	public Class<UpgradeRecipe> getRecipeClass() {
		return UpgradeRecipe.class;
	}

	@SuppressWarnings("removal")
	@Override
	public ResourceLocation getRecipeCategoryUid() {
		return getRecipeType().getUid();
	}

	@Override
	public boolean canHandle(DireSmithingTableContainer container, UpgradeRecipe recipe) {
		return true;
	}

	@Override
	public List<Slot> getRecipeSlots(DireSmithingTableContainer container, UpgradeRecipe recipe) {
		return List.of(container.getSlot(DireSmithingTableBE.BASE_SLOT), container.getSlot(DireSmithingTableBE.ADDITION_SLOT),
				container.getSlot(DireSmithingTableBE.TEMPLATE_SLOT));
	}

	@Override
	public List<Slot> getInventorySlots(DireSmithingTableContainer container, UpgradeRecipe recipe) {
		return container.slots.subList(DireSmithingTableBE.OUTPUT_SLOT + 1, container.slots.size());
	}
}
