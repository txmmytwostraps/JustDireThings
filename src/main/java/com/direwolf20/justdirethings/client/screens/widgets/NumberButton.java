package com.direwolf20.justdirethings.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class NumberButton extends BaseButton {
	private int value;
	public int min;
	public int max;

	public NumberButton(int x, int y, int width, int height, int value, int min, int max, Component localization, OnPress onPress) {
		super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY, onPress);
		this.value = value;
		this.localization = localization;
		this.min = min;
		this.max = max;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.enableDepthTest();
		fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0xFF353535);
		fill(poseStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0xFFD8D8D8);
		Font font = Minecraft.getInstance().font;
		poseStack.pushPose();
		float scale = 0.75f;
		poseStack.scale(scale, scale, scale);
		String msg = String.format("%,d", value);
		float x = (this.x + this.width / 2f) / scale - font.width(msg) / 2f;
		float y = (this.y + (this.height - font.lineHeight) / 2f / scale) / scale + 1;
		font.draw(poseStack, msg, x, y, Color.DARK_GRAY.getRGB());
		poseStack.popPose();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
