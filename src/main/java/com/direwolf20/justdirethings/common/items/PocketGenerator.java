package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.blocks.resources.CoalBlock_T1;
import com.direwolf20.justdirethings.common.capabilities.EnergyStorageItemStackNoReceive;
import com.direwolf20.justdirethings.common.capabilities.ItemStackItemHandler;
import com.direwolf20.justdirethings.common.containers.PocketGeneratorContainer;
import com.direwolf20.justdirethings.common.curios.CuriosIntegration;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.items.resources.Coal_T1;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.direwolf20.justdirethings.util.TooltipHelpers.*;

public class PocketGenerator extends Item implements PoweredItem, ToggleableItem {
	public static final String COUNTER = "counter";
	public static final String MAXBURN = "maxburn";
	public static final String FUELMULTIPLIER = "fuelmultiplier";
	public static final String FUEL_INVENTORY = "FuelInventory";

	public PocketGenerator() {
		super(new Properties()
				.tab(ModSetup.TAB_JUSTDIRETHINGS)
				.stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);

		if (!player.isShiftKeyDown()) {
			NetworkHooks.openGui((ServerPlayer) player, new SimpleMenuProvider(
					(windowId, playerInventory, playerEntity) -> new PocketGeneratorContainer(windowId, playerInventory, player, itemstack), new TranslatableComponent("")), (buf -> {
				buf.writeItem(itemstack);
			}));
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide) return;
		if (entity instanceof Player player && itemStack.getItem() instanceof ToggleableItem toggleableItem && toggleableItem.getEnabled(itemStack)) {
			IEnergyStorage energyStorage = itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
			if (energyStorage == null) return;
			if (energyStorage instanceof EnergyStorageItemStackNoReceive energyStorageItemStackNoReceive) {
				tryBurn(energyStorageItemStackNoReceive, itemStack);
				if (energyStorage.getEnergyStored() >= (getFEPerTick() / 10)) { //If we have 1/10th the max transfer speed, go ahead and let it rip
					for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
						ItemStack slotStack = player.getInventory().getItem(i);
						transferEnergy(slotStack, energyStorage);
					}
					IItemHandler curios = ModList.get().isLoaded("curios") ? CuriosIntegration.getEquippedCurios(player) : null;
					if (curios != null) {
						for (int i = 0; i < curios.getSlots(); i++) {
							ItemStack slotStack = curios.getStackInSlot(i);
							transferEnergy(slotStack, energyStorage);
						}
					}
				}
			}
		}
	}

	private void transferEnergy(ItemStack slotStack, IEnergyStorage energyStorage) {
		IEnergyStorage slotEnergy = slotStack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
		if (slotEnergy != null) {
			int acceptedEnergy = slotEnergy.receiveEnergy(getFEPerTick(), true);
			if (acceptedEnergy > 0) {
				int extractedEnergy = energyStorage.extractEnergy(acceptedEnergy, false);
				slotEnergy.receiveEnergy(extractedEnergy, false);
			}
		}
	}

	private int fePerTick(ItemStack itemStack) {
		return (getFePerFuelTick() * getBurnSpeedMultiplier(itemStack));
	}

	public void tryBurn(EnergyStorageItemStackNoReceive energyStorage, ItemStack itemStack) {
		boolean canInsertEnergy = energyStorage.forceReceiveEnergy(fePerTick(itemStack), true) > 0;
		if (NBTHelpers.getIntValue(itemStack, COUNTER) > 0 && canInsertEnergy) {
			burn(energyStorage, itemStack);
		} else if (canInsertEnergy) {
			if (initBurn(itemStack))
				burn(energyStorage, itemStack);
		}
	}

	private void burn(EnergyStorageItemStackNoReceive energyStorage, ItemStack itemStack) {
		energyStorage.forceReceiveEnergy(fePerTick(itemStack), false);
		int counter = NBTHelpers.getIntValue(itemStack, COUNTER);
		counter--;
		NBTHelpers.setIntValue(itemStack, COUNTER, counter);
		if (counter == 0) {
			NBTHelpers.setIntValue(itemStack, MAXBURN, 0);
			initBurn(itemStack);
		}
	}

	private boolean initBurn(ItemStack itemStack) {
		ItemStackItemHandler handler = new ItemStackItemHandler(itemStack, FUEL_INVENTORY, 1);
		ItemStack fuelStack = handler.getStackInSlot(0);

		int burnTime = ForgeHooks.getBurnTime(fuelStack, RecipeType.SMELTING);
		if (burnTime > 0) {
			if (fuelStack.getItem() instanceof Coal_T1 direCoal) {
				setFuelMultiplier(itemStack, direCoal.getBurnSpeedMultiplier());
			} else if (fuelStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoalBlock_T1 coalBlock) {
				setFuelMultiplier(itemStack, coalBlock.getBurnSpeedMultiplier());
			} else if (fuelStack.getItem() instanceof FuelCanister) {
				setFuelMultiplier(itemStack, FuelCanister.getBurnSpeedMultiplier(fuelStack));
			} else {
				setFuelMultiplier(itemStack, 1);
			}
			if (fuelStack.hasContainerItem())
				handler.setStackInSlot(0, fuelStack.getContainerItem());
			else {
				fuelStack.shrink(1);
				handler.setStackInSlot(0, fuelStack);
			}

			int counter = (int) (Math.floor(burnTime) / getBurnSpeedMultiplier(itemStack));
			int maxBurn = counter;
			NBTHelpers.setIntValue(itemStack, COUNTER, counter);
			NBTHelpers.setIntValue(itemStack, MAXBURN, maxBurn);
			return true;
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null) {
			return;
		}
		appendFEText(stack, tooltip);
		appendToolEnabled(stack, tooltip);
		appendGeneratorDetails(stack, tooltip);
		appendShiftForInfo(stack, tooltip);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return isPowerBarVisible(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return getPowerBarWidth(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		int color = getPowerBarColor(stack);
		if (color == -1)
			return super.getBarColor(stack);
		return color;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	public void setFuelMultiplier(ItemStack itemStack, int amount) {
		NBTHelpers.setIntValue(itemStack, FUELMULTIPLIER, amount);
	}

	public int getFuelMultiplier(ItemStack itemStack) {
		CompoundTag tag = itemStack.getTag();
		return tag != null && tag.contains(FUELMULTIPLIER) ? tag.getInt(FUELMULTIPLIER) : 1;
	}

	@Override
	public int getMaxEnergy() {
		return Config.POCKET_GENERATOR_MAX_FE.get();
	}

	public int getFEPerTick() {
		return Config.POCKET_GENERATOR_FE_PER_TICK.get();
	}

	public int getFePerFuelTick() {
		return Config.POCKET_GENERATOR_FE_PER_FUEL_TICK.get();
	}

	public int getBurnSpeedMultiplier(ItemStack itemStack) {
		return Config.POCKET_GENERATOR_BURN_SPEED_MULTIPLIER.get() * getFuelMultiplier(itemStack);
	}
}
