package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.client.screens.basescreens.BaseMachineScreen;
import com.direwolf20.justdirethings.client.screens.standardbuttons.ToggleButtonFactory;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.client.screens.widgets.ToggleButton;
import com.direwolf20.justdirethings.common.blockentities.BlockBreakerT1BE;
import com.direwolf20.justdirethings.common.containers.BlockBreakerT2Container;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.BreakerPayload;
import com.direwolf20.justdirethings.common.network.data.DirectionSettingPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BlockBreakerT2Screen extends BaseMachineScreen<BlockBreakerT2Container> {
	public boolean sneaking;

	public BlockBreakerT2Screen(BlockBreakerT2Container container, Inventory inv, Component name) {
		super(container, inv, name);
		if (baseMachineBE instanceof BlockBreakerT1BE breaker) {
			sneaking = breaker.sneaking;
		}
	}

	@Override
	public void setTopSection() {
		extraWidth = 60;
		extraHeight = 0;
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(ToggleButtonFactory.DIRECTIONBUTTON(getGuiLeft() + 116, topSectionTop + 62, direction, b -> {
			direction = ((ToggleButton) b).getTexturePosition();
			PacketHandler.CHANNEL.sendToServer(new DirectionSettingPayload(direction));
		}));
		addRenderableWidget(ToggleButtonFactory.SNEAKCLICKBUTTON(getGuiLeft() + 8, topSectionTop + 44, sneaking, b -> {
			sneaking = !sneaking;
			((GrayscaleButton) b).toggleActive();
			saveSettings();
		}));
	}

	@Override
	protected void drawMachineSlot(PoseStack poseStack, Slot slot) {
		ItemStack itemStack = slot.getItem();
		if (itemStack.isEmpty()) {
			RenderSystem.setShaderTexture(0, JUSTSLOT);
			blit(poseStack, getGuiLeft() + slot.x - 1, getGuiTop() + slot.y - 1, 18, 0, 18, 18);
		} else {
			super.drawMachineSlot(poseStack, slot);
		}
	}

	@Override
	public void saveSettings() {
		super.saveSettings();
		PacketHandler.CHANNEL.sendToServer(new BreakerPayload(sneaking));
	}
}
