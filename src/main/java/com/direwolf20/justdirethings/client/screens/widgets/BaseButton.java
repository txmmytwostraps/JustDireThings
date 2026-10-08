package com.direwolf20.justdirethings.client.screens.widgets;

import com.direwolf20.justdirethings.util.MiscTools;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class BaseButton extends Button {
	protected Component localization = TextComponent.EMPTY;

	protected BaseButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
		super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
	}

	public Component getLocalization() {
		return localization;
	}

	public Component getLocalization(int mouseX, int mouseY) {
		if (MiscTools.inBounds(x, y, getWidth(), getHeight(), mouseX, mouseY))
			return getLocalization();
		return TextComponent.EMPTY;
	}
}
