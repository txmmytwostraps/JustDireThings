package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.common.blockentities.DireSmithingTableBE;
import com.direwolf20.justdirethings.common.containers.DireSmithingTableContainer;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class DireSmithingTableScreen extends AbstractContainerScreen<DireSmithingTableContainer> {
	private static final ResourceLocation SMITHING_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/smithing.png");
	private static final int PANEL_COLOR = 0xFFC6C6C6;
	private static final Component MISSING_TEMPLATE_TOOLTIP = new TranslatableComponent("justdirethings.screen.missing_template");
	private static final Component ERROR_TOOLTIP = new TranslatableComponent("justdirethings.screen.upgrade_error");
	private static final Quaternion ARMOR_STAND_ANGLE = Quaternion.fromXYZ(0.43633232F, 0.0F, (float) Math.PI);
	protected DireSmithingTableBE direSmithingTableBE;
	private ArmorStand armorStandPreview;
	private ItemStack armorStandItem = ItemStack.EMPTY;

	public DireSmithingTableScreen(DireSmithingTableContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		this.titleLabelX = 44;
		this.titleLabelY = 15;
		if (container.baseMachineBE instanceof DireSmithingTableBE direSmithingTableBE) {
			this.direSmithingTableBE = direSmithingTableBE;
		}
	}

	@Override
	protected void init() {
		super.init();
		this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
		this.armorStandPreview.getEntityData().set(ArmorStand.DATA_CLIENT_FLAGS, (byte) (ArmorStand.CLIENT_FLAG_SHOW_ARMS | ArmorStand.CLIENT_FLAG_NO_BASEPLATE));
		this.armorStandPreview.yBodyRot = 210.0F;
		this.armorStandPreview.setXRot(25.0F);
		this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
		this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
		this.updateArmorStandPreview(this.menu.getSlot(DireSmithingTableBE.OUTPUT_SLOT).getItem());
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		// No client listener fires for server slot changes, so poll the output
		ItemStack output = this.menu.getSlot(DireSmithingTableBE.OUTPUT_SLOT).getItem();
		if (!ItemStack.matches(output, armorStandItem))
			updateArmorStandPreview(output);
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
		renderOnboardingTooltips(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, SMITHING_LOCATION);
		blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		// The texture is laid out for two inputs. Clear it and redraw for three.
		fill(poseStack, leftPos + 17, topPos + 7, leftPos + 151, topPos + 64, PANEL_COLOR);
		// Draw row 22 of the hammer head twice, like the three-input screen
		blit(poseStack, leftPos + 7, topPos + 7, 17, 7, 30, 16);
		blit(poseStack, leftPos + 7, topPos + 23, 17, 22, 30, 15);
		for (int i = DireSmithingTableBE.TEMPLATE_SLOT; i <= DireSmithingTableBE.OUTPUT_SLOT; i++) {
			Slot slot = this.menu.getSlot(i);
			blit(poseStack, leftPos + slot.x - 1, topPos + slot.y - 1, 26, 46, 18, 18);
		}
		blit(poseStack, leftPos + 68, topPos + 49, 102, 48, 22, 15);
		if (hasRecipeError())
			blit(poseStack, leftPos + 65, topPos + 46, 176, 0, 28, 21);
		renderArmorStandPreview(leftPos + 141, topPos + 75, 25);
	}

	private void updateArmorStandPreview(ItemStack stack) {
		armorStandItem = stack.copy();
		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			this.armorStandPreview.setItemSlot(equipmentSlot, ItemStack.EMPTY);
		}

		if (!stack.isEmpty()) {
			ItemStack itemstack = stack.copy();
			if (stack.getItem() instanceof ArmorItem armorItem) {
				this.armorStandPreview.setItemSlot(armorItem.getSlot(), itemstack);
			} else {
				this.armorStandPreview.setItemSlot(EquipmentSlot.OFFHAND, itemstack);
			}
		}
	}

	// Not renderEntityInInventory, which turns it toward the mouse
	private void renderArmorStandPreview(int x, int y, int scale) {
		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();
		modelViewStack.translate(x, y, 1050.0D);
		modelViewStack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack entityPoseStack = new PoseStack();
		entityPoseStack.translate(0.0D, 0.0D, 1000.0D);
		entityPoseStack.scale(scale, scale, scale);
		entityPoseStack.mulPose(ARMOR_STAND_ANGLE);
		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		entityRenderDispatcher.setRenderShadow(false);
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(this.armorStandPreview, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, entityPoseStack, bufferSource, 15728880));
		bufferSource.endBatch();
		entityRenderDispatcher.setRenderShadow(true);
		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}

	private void renderOnboardingTooltips(PoseStack poseStack, int mouseX, int mouseY) {
		Optional<Component> optional = Optional.empty();
		if (hasRecipeError() && isHovering(65, 46, 28, 21, mouseX, mouseY))
			optional = Optional.of(ERROR_TOOLTIP);
		if (hoveredSlot != null && hoveredSlot.index == DireSmithingTableBE.TEMPLATE_SLOT && !hoveredSlot.hasItem() && !isUpgradeWithoutTemplate())
			optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
		optional.ifPresent(component -> renderTooltip(poseStack, font.split(component, 115), mouseX, mouseY));
	}

	// Vanilla smithing needs no template, so don't ask for one
	private boolean isUpgradeWithoutTemplate() {
		ItemStack[] inputs = getInputs();
		return (!inputs[DireSmithingTableBE.BASE_SLOT].isEmpty() || !inputs[DireSmithingTableBE.ADDITION_SLOT].isEmpty())
				&& direSmithingTableBE.acceptsUpgradeInputs(inputs);
	}

	// Inputs that make nothing. The output slot can't tell us, so ask the recipes.
	private boolean hasRecipeError() {
		ItemStack[] inputs = getInputs();
		ItemStack base = inputs[DireSmithingTableBE.BASE_SLOT];
		ItemStack addition = inputs[DireSmithingTableBE.ADDITION_SLOT];
		if (base.isEmpty() || addition.isEmpty())
			return false;
		if (inputs[DireSmithingTableBE.TEMPLATE_SLOT].isEmpty())
			return !direSmithingTableBE.acceptsInputs(inputs) && (direSmithingTableBE.isUpgradeInput(DireSmithingTableBE.BASE_SLOT, base)
					|| direSmithingTableBE.isUpgradeInput(DireSmithingTableBE.ADDITION_SLOT, addition));
		return direSmithingTableBE.getCraftResult(new SimpleContainer(inputs)).isEmpty();
	}

	private ItemStack[] getInputs() {
		return new ItemStack[]{menu.getSlot(DireSmithingTableBE.TEMPLATE_SLOT).getItem(), menu.getSlot(DireSmithingTableBE.BASE_SLOT).getItem(),
				menu.getSlot(DireSmithingTableBE.ADDITION_SLOT).getItem()};
	}
}
