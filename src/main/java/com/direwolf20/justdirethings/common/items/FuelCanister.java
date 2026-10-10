package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.blocks.resources.CoalBlock_T1;
import com.direwolf20.justdirethings.common.containers.FuelCanisterContainer;
import com.direwolf20.justdirethings.common.items.resources.Coal_T1;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.util.MagicHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FuelCanister extends Item {
	private static final String FUEL_LEVEL = "FuelLevel";
	private static final String BURN_SPEED = "BurnSpeed";

	public FuelCanister() {
		super(new Properties()
				.tab(ModSetup.TAB_JUSTDIRETHINGS)
				.stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null) {
			return;
		}

		boolean sneakPressed = Screen.hasShiftDown();
		if (sneakPressed)
			tooltip.add(new TranslatableComponent("justdirethings.fuelcanisteramt", MagicHelpers.formatted(getFuelLevel(stack))).withStyle(ChatFormatting.AQUA));
		else
			tooltip.add(new TranslatableComponent("justdirethings.fuelcanisteritemsamt", MagicHelpers.formatted(((float) getFuelLevel(stack) / 200))).withStyle(ChatFormatting.AQUA));

	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);

		NetworkHooks.openGui((ServerPlayer) player, new SimpleMenuProvider(
				(windowId, playerInventory, playerEntity) -> new FuelCanisterContainer(windowId, playerInventory, player, itemstack), new TranslatableComponent("")), (buf -> {
			buf.writeItem(itemstack);
		}));

		return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
	}

	@Override
	public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return getFuelLevel(stack) >= Config.FUEL_CANISTER_MINIMUM_TICKS_CONSUMED.get() ? Config.FUEL_CANISTER_MINIMUM_TICKS_CONSUMED.get() : 0;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		ItemStack copy = stack.copy();
		decrementFuel(copy);
		return copy;
	}

	public static int getFuelLevel(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag == null ? 0 : tag.getInt(FUEL_LEVEL);
	}

	public static void setFuelLevel(ItemStack stack, int fuelLevel) {
		stack.getOrCreateTag().putInt(FUEL_LEVEL, fuelLevel);
	}

	public static double getBurnSpeed(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag != null && tag.contains(BURN_SPEED) ? tag.getDouble(BURN_SPEED) : 1.0;
	}

	public static int getBurnSpeedMultiplier(ItemStack stack) {
		double burnSpeed = getBurnSpeed(stack);
		return (int) Math.round(burnSpeed);
	}

	public static void setBurnSpeed(ItemStack stack, double burnSpeed) {
		stack.getOrCreateTag().putDouble(BURN_SPEED, burnSpeed);
	}

	public static void decrementFuel(ItemStack stack) {
		int currentFuel = getFuelLevel(stack);
		if (currentFuel >= Config.FUEL_CANISTER_MINIMUM_TICKS_CONSUMED.get()) //Should always be true but lets be sure!
			currentFuel = currentFuel - Config.FUEL_CANISTER_MINIMUM_TICKS_CONSUMED.get();
		setFuelLevel(stack, currentFuel);
	}

	public static double calculateBurnSpeed(int currentFuelLevel, double currentBurnSpeedMultiplier, int newFuelLevel, double newFuelMultiplier) {
		double totalFuel = currentFuelLevel + newFuelLevel;
		// Calculate the weighted average of the multipliers based on the fuel levels.
		double newBurnSpeedMultiplier = ((currentFuelLevel * currentBurnSpeedMultiplier) + (newFuelLevel * newFuelMultiplier)) / totalFuel;
		return newBurnSpeedMultiplier;
	}

	public static void incrementFuel(ItemStack stack, ItemStack fuelStack) {
		int currentFuel = getFuelLevel(stack);
		int fuelPerPiece = ForgeHooks.getBurnTime(fuelStack, RecipeType.SMELTING);
		if (fuelPerPiece == 0) return;
		double currentBurnSpeedMultiplier = getBurnSpeed(stack);
		int fuelMultiplier = 1;
		if (fuelStack.getItem() instanceof Coal_T1 direCoal)
			fuelMultiplier = direCoal.getBurnSpeedMultiplier();
		else if (fuelStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoalBlock_T1 coalBlock)
			fuelMultiplier = coalBlock.getBurnSpeedMultiplier();
		int totalNewFuel = 0;
		while ((currentFuel + totalNewFuel) + fuelPerPiece <= Config.FUEL_CANISTER_MAXIMUM_FUEL.get() && !fuelStack.isEmpty()) {
			totalNewFuel += fuelPerPiece;
			fuelStack.shrink(1); // Consume one unit of the fuel stack.
		}

		if (totalNewFuel > 0) {
			currentBurnSpeedMultiplier = calculateBurnSpeed(currentFuel, currentBurnSpeedMultiplier, totalNewFuel, fuelMultiplier);
		}

		setFuelLevel(stack, currentFuel + totalNewFuel);
		setBurnSpeed(stack, currentBurnSpeedMultiplier);
	}

}
