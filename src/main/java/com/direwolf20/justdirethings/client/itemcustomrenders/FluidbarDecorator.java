package com.direwolf20.justdirethings.client.itemcustomrenders;

import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;

public class FluidbarDecorator {
	/**
	 * Runs after the vanilla item decorations.
	 */
	public static void render(ItemStack stack, int xOffset, int yOffset) {
		if (stack.getItem() instanceof FluidContainingItem fluidContainingItem) {
			// Retrieve energy capability
			boolean isPowerBarVisible = false;
			if (stack.getItem() instanceof PoweredItem poweredItem) {
				isPowerBarVisible = poweredItem.isPowerBarVisible(stack);
			}
			if (stack.isBarVisible())
				isPowerBarVisible = true;

			// Retrieve fluid capability
			boolean isFluidBarVisible = fluidContainingItem.isFluidBarVisible(stack);

			if (!isFluidBarVisible) {
				return; // Do not render if fluid is at max capacity
			}

			// Calculate positions based on whether the power bar is visible
			int fluidBarY = isPowerBarVisible ? yOffset + 11 : yOffset + 13; // Adjust Y position based on power bar visibility
			int fluidBarWidth = fluidContainingItem.getFluidBarWidth(stack);
			int fluidBarColor = fluidContainingItem.getFluidBarColor(stack);

			// Render fluid bar
			renderBar(new PoseStack(), xOffset + 2, fluidBarY, fluidBarWidth, fluidBarColor);
		}
	}

	private static void renderBar(PoseStack poseStack, int x, int y, int width, int color) {
		// Over the item, like the durability bar
		RenderSystem.disableDepthTest();

		// Render the background of the bar (black)
		GuiComponent.fill(poseStack, x, y, x + 13, y + 2, 0xFF303030);

		// Render the fluid bar with the calculated width and color
		GuiComponent.fill(poseStack, x, y, x + width, y + 1, color | 0xFF000000);

		RenderSystem.enableDepthTest();
	}
}
