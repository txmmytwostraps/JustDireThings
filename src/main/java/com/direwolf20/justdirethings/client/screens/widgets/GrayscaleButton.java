package com.direwolf20.justdirethings.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class GrayscaleButton extends BaseButton {
	private ResourceLocation texture;
	private boolean buttonActive;

	public GrayscaleButton(int x, int y, int width, int height, ResourceLocation texture, OnPress onPress) {
		super(x, y, width, height, TextComponent.EMPTY, onPress);
		this.texture = texture;
		this.buttonActive = true; //AlwaysActive
	}

	public GrayscaleButton(int x, int y, int width, int height, ResourceLocation texture, Component localization, boolean active, OnPress onPress) {
		super(x, y, width, height, TextComponent.EMPTY, onPress);
		this.texture = texture;
		this.buttonActive = active;
		this.localization = localization;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		if (buttonActive)
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		else
			RenderSystem.setShaderColor(0.33f, 0.33f, 0.33f, 1.0f);
		RenderSystem.setShaderTexture(0, texture);
		blit(poseStack, this.x, this.y, 0, 0, width, height, width, height);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}

	public void toggleActive() {
		buttonActive = !buttonActive;
	}
}
