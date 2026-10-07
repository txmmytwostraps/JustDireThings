package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.common.fluids.unstableportalfluid.UnstablePortalFluidAttributes;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseBow;
import com.direwolf20.justdirethings.datagen.recipes.FluidDropRecipe;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EntityEvents {
	public record FluidInputs(BlockState blockState, Item item) {
	}

	static Map<FluidInputs, BlockState> fluidCraftCache = new HashMap<>();

	@SubscribeEvent
	public static void livingUseItem(LivingEntityUseItemEvent.Start event) {
		if (event.getEntityLiving() instanceof Player && event.getItem().getItem() instanceof BaseBow baseBow) {
			event.setDuration(event.getDuration() - (20 - (int) baseBow.getMaxDraw()));
		}
	}

	/**
	 * Called from ItemEntityMixin each tick, on both sides.
	 */
	public static void handleFluidDrop(ItemEntity itemEntity) {
		Level level = itemEntity.level;
		BlockState blockState = itemEntity.getFeetBlockState();
		if (!(blockState.getBlock() instanceof LiquidBlock)) return;
		BlockState fluidDropOutput = findRecipe(blockState, itemEntity);
		if (fluidDropOutput != null && !fluidDropOutput.isAir()) {
			BlockPos blockPos = itemEntity.blockPosition();
			if (level.setBlockAndUpdate(blockPos, fluidDropOutput)) {
				itemEntity.getItem().shrink(1);
				FluidStack fluidStack = new FluidStack(level.getFluidState(blockPos).getType(), 1000);
				if (UnstablePortalFluidAttributes.vaporizesOnPlacement(level, blockPos, fluidStack)) {
					level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
					fluidStack.getFluid().getAttributes().vaporize(null, level, blockPos, fluidStack);
				} else {
					if (!level.isClientSide)
						level.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
		}
	}

	@Nullable
	private static BlockState findRecipe(BlockState blockState, ItemEntity entity) {
		FluidInputs fluidInputs = new FluidInputs(blockState, entity.getItem().getItem());
		if (fluidCraftCache.containsKey(fluidInputs))
			return fluidCraftCache.get(fluidInputs);
		RecipeManager recipeManager = entity.level.getRecipeManager();

		for (FluidDropRecipe fluidDropRecipe : recipeManager.getAllRecipesFor(Registration.FLUID_DROP_RECIPE_TYPE.get())) {
			if (fluidDropRecipe.matches(blockState, entity.getItem())) {
				fluidCraftCache.put(fluidInputs, fluidDropRecipe.getOutput());
				break;
			}
		}
		if (!fluidCraftCache.containsKey(fluidInputs))
			fluidCraftCache.put(fluidInputs, Blocks.AIR.defaultBlockState());
		return fluidCraftCache.get(fluidInputs);
	}

	private static void clearCache() {
		fluidCraftCache.clear();
	}

	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent e) {
		clearCache();
	}

	@SubscribeEvent
	public static void onReloadServerResources(AddReloadListenerEvent e) {
		clearCache();
	}

	@SubscribeEvent
	public static void onClientRecipesUpdated(RecipesUpdatedEvent e) {
		clearCache();
	}
}
