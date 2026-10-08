package com.direwolf20.justdirethings.client.screens.standardbuttons;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.client.screens.widgets.TextWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ValueButtons {
	private static final ResourceLocation add = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/buttons/add.png");
	private static final ResourceLocation subtract = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/buttons/remove.png");
	private static final int STANDARD_WIDTH = 12; // Example width
	private static final int STANDARD_HEIGHT = 12; // Example height
	private int value;
	private final int minValue;
	private final int maxValue;
	private final TextWidget valueDisplay;
	public List<AbstractWidget> widgetList = new ArrayList<>();

	public ValueButtons(int x, int y, int startingValue, int minValue, int maxValue, Font font, BiConsumer<ValueButtons, Integer> onPress) {
		this(x, y, startingValue, minValue, maxValue, TextComponent.EMPTY, font, onPress);
	}

	public ValueButtons(int x, int y, int startingValue, int minValue, int maxValue, Component label, Font font, BiConsumer<ValueButtons, Integer> onPress) {
		this.value = startingValue;
		this.minValue = minValue;
		this.maxValue = maxValue;

		// Value Display
		this.valueDisplay = new TextWidget(x + 11, y + 2, 20, font.lineHeight, new TextComponent(String.valueOf(startingValue)), font);
		widgetList.add(this.valueDisplay);

		widgetList.add(new TextWidget(x, y - 8, 40, font.lineHeight, label, font));

		// Minus Button
		widgetList.add(new GrayscaleButton(x, y, STANDARD_WIDTH, STANDARD_HEIGHT, subtract, button -> {
			decrementValue();
			if (onPress != null) onPress.accept(this, this.value);
		}));

		// Plus Button
		widgetList.add(new GrayscaleButton(x + 30, y, STANDARD_WIDTH, STANDARD_HEIGHT, add, button -> {
			incrementValue();
			if (onPress != null) onPress.accept(this, this.value);
		}));
	}

	private void incrementValue() {
		if (value < maxValue) {
			value++;
			updateDisplay();
		}
	}

	private void decrementValue() {
		if (value > minValue) {
			value--;
			updateDisplay();
		}
	}

	private void updateDisplay() {
		this.valueDisplay.setMessage(new TextComponent(String.valueOf(value)));
	}
}
