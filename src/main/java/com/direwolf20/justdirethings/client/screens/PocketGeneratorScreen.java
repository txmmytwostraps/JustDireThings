package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.blocks.resources.CoalBlock_T1;
import com.direwolf20.justdirethings.common.containers.PocketGeneratorContainer;
import com.direwolf20.justdirethings.common.items.FuelCanister;
import com.direwolf20.justdirethings.common.items.PocketGenerator;
import com.direwolf20.justdirethings.common.items.resources.Coal_T1;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.NBTHelpers;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Arrays;
import java.util.List;

public class PocketGeneratorScreen extends AbstractContainerScreen<PocketGeneratorContainer> {
	private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/pocketgenerator.png");

	protected final PocketGeneratorContainer container;
	private ItemStack pocketGenerator;
	private IEnergyStorage energyStorage;

	public PocketGeneratorScreen(PocketGeneratorContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		this.container = container;
		this.pocketGenerator = container.playerEntity.getMainHandItem();
		this.energyStorage = pocketGenerator.getCapability(CapabilityEnergy.ENERGY).orElse(null);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
		if (mouseX > (leftPos + 7) && mouseX < (leftPos + 7) + 18 && mouseY > (topPos + 7) && mouseY < (topPos + 7) + 73) {
			int counter = NBTHelpers.getIntValue(pocketGenerator, PocketGenerator.COUNTER);
			int feBurnPerTick = 0;
			if (pocketGenerator.getItem() instanceof PocketGenerator pocketGeneratorItem) {
				feBurnPerTick = pocketGeneratorItem.getFePerFuelTick() * pocketGeneratorItem.getBurnSpeedMultiplier(pocketGenerator);
			}
			if (hasShiftDown())
				renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(
						new TranslatableComponent("justdirethings.screen.energy", MagicHelpers.formatted(energyStorage.getEnergyStored()), MagicHelpers.withSuffix(energyStorage.getMaxEnergyStored())),
						counter <= 0 ?
								new TranslatableComponent("justdirethings.screen.no_fuel") :
								new TranslatableComponent("justdirethings.screen.burn_time", MagicHelpers.ticksInSeconds(counter)),
						counter > 0 ?
								new TranslatableComponent("justdirethings.screen.fepertick", MagicHelpers.formatted(feBurnPerTick)) :
								new TranslatableComponent("justdirethings.screen.fepertick", MagicHelpers.formatted(0))
				)), mouseX, mouseY);
			else
				renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(
						new TranslatableComponent("justdirethings.screen.energy", MagicHelpers.withSuffix(energyStorage.getEnergyStored()), MagicHelpers.withSuffix(energyStorage.getMaxEnergyStored())),
						counter <= 0 ?
								new TranslatableComponent("justdirethings.screen.no_fuel") :
								new TranslatableComponent("justdirethings.screen.burn_time", MagicHelpers.ticksInSeconds(counter)),
						counter > 0 ?
								new TranslatableComponent("justdirethings.screen.fepertick", MagicHelpers.formatted(feBurnPerTick)) :
								new TranslatableComponent("justdirethings.screen.fepertick", MagicHelpers.formatted(0))
				)), mouseX, mouseY);
		}
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
		if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
			ItemStack fuelStack = this.hoveredSlot.getItem();
			int burnTime = ForgeHooks.getBurnTime(fuelStack, RecipeType.SMELTING);
			if (burnTime > 0) {
				int fuelBurnMultiplier = 1;
				if (fuelStack.getItem() instanceof Coal_T1 direCoal) {
					fuelBurnMultiplier = direCoal.getBurnSpeedMultiplier();
				} else if (fuelStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoalBlock_T1 coalBlock) {
					fuelBurnMultiplier = coalBlock.getBurnSpeedMultiplier();
				} else if (fuelStack.getItem() instanceof FuelCanister) {
					fuelBurnMultiplier = FuelCanister.getBurnSpeedMultiplier(fuelStack);
				}
				List<Component> tooltip = this.getTooltipFromItem(fuelStack);
				tooltip.add(new TranslatableComponent("justdirethings.screen.burnspeedmultiplier", fuelBurnMultiplier).withStyle(ChatFormatting.RED));
				renderTooltip(poseStack, tooltip, fuelStack.getTooltipImage(), pX, pY, fuelStack);
				return;
			}
		}
		super.renderTooltip(poseStack, pX, pY);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		//No-Op
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

		this.pocketGenerator = container.playerEntity.getMainHandItem();
		if (pocketGenerator.isEmpty() || !(pocketGenerator.getItem() instanceof PocketGenerator))
			return;
		this.energyStorage = pocketGenerator.getCapability(CapabilityEnergy.ENERGY).orElse(null);
		if (energyStorage == null)
			return;
		int maxBurn = NBTHelpers.getIntValue(pocketGenerator, PocketGenerator.MAXBURN);
		int counter = NBTHelpers.getIntValue(pocketGenerator, PocketGenerator.COUNTER);
		int maxHeight = 13;
		if (maxBurn > 0) {
			int remaining = (counter * maxHeight) / maxBurn;
			blit(poseStack, leftPos + 80, topPos + 17 + 13 - remaining, 176, 13 - remaining, 14, remaining + 1);
		}

		int maxEnergy = energyStorage.getMaxEnergyStored();
		int height = 70;
		if (maxEnergy > 0) {
			int remaining = (energyStorage.getEnergyStored() * height) / maxEnergy;
			blit(poseStack, leftPos + 8, topPos + 78 - remaining, 176, 84 - remaining, 16, remaining + 1);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		InputConstants.Key mouseKey = InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_);
		if (p_keyPressed_1_ == 256 || minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
			onClose();

			return true;
		}

		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
}
