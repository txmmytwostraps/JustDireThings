package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.containers.PotionCanisterContainer;
import com.direwolf20.justdirethings.common.items.PotionCanister;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.MiscTools;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotionCanisterScreen extends AbstractContainerScreen<PotionCanisterContainer> {
	private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/fuelcanister.png");
	protected final ResourceLocation FLUIDBAR = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/fluidbar.png");

	private ItemStack potionCanister;

	public PotionCanisterScreen(PotionCanisterContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		this.potionCanister = container.potionCanister;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
		super.renderTooltip(poseStack, pX, pY);
		fluidBarTooltip(poseStack, pX, pY);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

		int offset = 5;
		RenderSystem.setShaderTexture(0, FLUIDBAR);
		blit(poseStack, getGuiLeft() + offset, getGuiTop() + 5, 0, 0, 18, 72, 36, 72);
		int maxMB = PotionCanister.getMaxMB(), height = 70;
		if (maxMB > 0) {
			int remaining = (PotionCanister.getPotionAmount(potionCanister) * height) / maxMB;
			renderFluid(poseStack, getGuiLeft() + offset + 1, getGuiTop() + 5 + 72 - 1, 16, remaining, potionCanister);
		}
		RenderSystem.setShaderTexture(0, FLUIDBAR);
		blit(poseStack, getGuiLeft() + offset, getGuiTop() + 5, 18, 0, 18, 72, 36, 72);
	}

	public void renderFluid(PoseStack poseStack, int startX, int startY, int width, int height, ItemStack potionCanister) {
		ItemStack potionContents = PotionCanister.getPotionContents(potionCanister);
		if (potionContents.isEmpty() || PotionCanister.getPotionAmount(potionCanister) <= 0) return;
		ResourceLocation fluidStill = Fluids.WATER.getAttributes().getStillTexture();
		TextureAtlasSprite fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
		int fluidColor = PotionCanister.getPotionContentsColor(potionContents);

		float red = (float) (fluidColor >> 16 & 255) / 255.0F;
		float green = (float) (fluidColor >> 8 & 255) / 255.0F;
		float blue = (float) (fluidColor & 255) / 255.0F;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

		poseStack.pushPose();
		RenderSystem.setShaderColor(red, green, blue, 1.0f);

		int zLevel = 0;
		float uMin = fluidStillSprite.getU0();
		float uMax = fluidStillSprite.getU1();
		float vMin = fluidStillSprite.getV0();
		float vMax = fluidStillSprite.getV1();
		int textureWidth = fluidStillSprite.getWidth();
		int textureHeight = fluidStillSprite.getHeight();

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder vertexBuffer = tesselator.getBuilder();
		vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		int yOffset = 0;
		while (yOffset < height) {
			int drawHeight = Math.min(textureHeight, height - yOffset);
			int drawY = startY - yOffset - drawHeight; // Adjust for bottom-to-top drawing

			float vMaxAdjusted = vMin + (vMax - vMin) * ((float) drawHeight / textureHeight);

			int xOffset = 0;
			while (xOffset < width) {
				int drawWidth = Math.min(textureWidth, width - xOffset);

				float uMaxAdjusted = uMin + (uMax - uMin) * ((float) drawWidth / textureWidth);

				vertexBuffer.vertex(poseStack.last().pose(), startX + xOffset, drawY + drawHeight, zLevel).uv(uMin, vMaxAdjusted).endVertex();
				vertexBuffer.vertex(poseStack.last().pose(), startX + xOffset + drawWidth, drawY + drawHeight, zLevel).uv(uMaxAdjusted, vMaxAdjusted).endVertex();
				vertexBuffer.vertex(poseStack.last().pose(), startX + xOffset + drawWidth, drawY, zLevel).uv(uMaxAdjusted, vMin).endVertex();
				vertexBuffer.vertex(poseStack.last().pose(), startX + xOffset, drawY, zLevel).uv(uMin, vMin).endVertex();

				xOffset += drawWidth;
			}
			yOffset += drawHeight;
		}

		tesselator.end();
		poseStack.popPose();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.applyModelViewMatrix();
	}

	public void fluidBarTooltip(PoseStack poseStack, int pX, int pY) {
		ItemStack potionContents = PotionCanister.getPotionContents(potionCanister);
		int potionAmt = PotionCanister.getPotionAmount(potionCanister);
		if (potionAmt == 0 || potionContents.isEmpty()) return;
		if (MiscTools.inBounds(getGuiLeft() + 5, getGuiTop() + 5, 18, 72, pX, pY)) {
			List<Component> components = new ArrayList<>();
			components.add(new TextComponent(MagicHelpers.formatted(potionAmt) + "/" + MagicHelpers.formatted(PotionCanister.getMaxMB())));
			PotionUtils.addPotionTooltip(potionContents, components, 1.0F);
			renderTooltip(poseStack, components, Optional.empty(), pX, pY);
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
