package com.direwolf20.justdirethings.client.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TextWidget extends AbstractWidget {
	private final Font font;

	public TextWidget(int x, int y, int width, int height, Component message, Font font) {
		super(x, y, width, height, message);
		this.font = font;
		this.active = false;
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		drawCenteredString(poseStack, font, getMessage(), x + width / 2, y + (height - font.lineHeight) / 2, 0xFFFFFF);
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
	}
}
