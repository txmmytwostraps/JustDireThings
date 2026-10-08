package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.containers.FuelCanisterContainer;
import com.direwolf20.justdirethings.common.items.FuelCanister;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

import java.awt.*;
import java.util.List;

public class FuelCanisterScreen extends AbstractContainerScreen<FuelCanisterContainer> {
	private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/fuelcanister.png");

	protected final FuelCanisterContainer container;
	private ItemStack fuelCanister;

	public FuelCanisterScreen(FuelCanisterContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		this.container = container;
		this.fuelCanister = container.fuelCanisterItemstack;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
		MutableComponent msg = new TranslatableComponent("justdirethings.fuelcanisteritemsamt", MagicHelpers.formatted((float) FuelCanister.getFuelLevel(fuelCanister) / 200));
		font.draw(poseStack, msg, this.getGuiLeft() + this.imageWidth / 2 - font.width(msg) / 2, getGuiTop() + 5, Color.DARK_GRAY.getRGB());
		msg = new TranslatableComponent("justdirethings.fuelcanisteramt", MagicHelpers.formatted(FuelCanister.getFuelLevel(fuelCanister)));
		font.draw(poseStack, msg, (this.getGuiLeft() + this.imageWidth / 2) - font.width(msg) / 2, getGuiTop() + 15, Color.DARK_GRAY.getRGB());
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
		if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
			ItemStack itemstack = this.hoveredSlot.getItem();
			int fuelPerPiece = ForgeHooks.getBurnTime(itemstack, RecipeType.SMELTING);
			List<Component> tooltip = this.getTooltipFromItem(itemstack);
			if (container.handler.isItemValid(0, itemstack)) {
				if (hasShiftDown()) {
					tooltip.add(new TranslatableComponent("justdirethings.fuelcanisteramt", MagicHelpers.formatted(fuelPerPiece)).withStyle(ChatFormatting.AQUA));
					tooltip.add(new TranslatableComponent("justdirethings.fuelcanisteramtstack", MagicHelpers.formatted(fuelPerPiece * itemstack.getCount())).withStyle(ChatFormatting.AQUA));
				} else {
					tooltip.add(new TranslatableComponent("justdirethings.fuelcanisteritemsamt", MagicHelpers.formatted((float) fuelPerPiece / 200)).withStyle(ChatFormatting.AQUA));
					tooltip.add(new TranslatableComponent("justdirethings.fuelcanisteritemsamtstack", MagicHelpers.formatted((float) (fuelPerPiece * itemstack.getCount()) / 200)).withStyle(ChatFormatting.AQUA));
				}
			}
			renderTooltip(poseStack, tooltip, itemstack.getTooltipImage(), pX, pY, itemstack);
		}
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		//Labels draw after the slots, so the red overlay goes here
		RenderSystem.disableDepthTest();
		for (Slot slot : this.menu.slots) {
			if (slot.isActive() && hasInvalidItem(slot))
				fill(poseStack, slot.x, slot.y, slot.x + 16, slot.y + Mth.ceil(16.0F), 0x7FFF0000);
		}
		RenderSystem.enableDepthTest();
		if (this.hoveredSlot != null && hasInvalidItem(this.hoveredSlot))
			renderSlotHighlight(poseStack, this.hoveredSlot.x, this.hoveredSlot.y, this.getBlitOffset(), super.getSlotColor(this.hoveredSlot.index));
		super.renderLabels(poseStack, mouseX, mouseY);
	}

	@Override
	public int getSlotColor(int index) {
		//Red slots get their highlight in renderLabels, over the overlay
		return hasInvalidItem(this.menu.slots.get(index)) ? 0 : super.getSlotColor(index);
	}

	private boolean hasInvalidItem(Slot slot) {
		return !slot.getItem().isEmpty() && !container.handler.isItemValid(slot.getSlotIndex(), slot.getItem());
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
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
