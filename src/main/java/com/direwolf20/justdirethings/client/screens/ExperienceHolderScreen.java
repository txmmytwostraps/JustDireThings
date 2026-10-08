package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.client.screens.basescreens.BaseMachineScreen;
import com.direwolf20.justdirethings.client.screens.standardbuttons.ToggleButtonFactory;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.client.screens.widgets.NumberButton;
import com.direwolf20.justdirethings.common.blockentities.ExperienceHolderBE;
import com.direwolf20.justdirethings.common.containers.ExperienceHolderContainer;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.ExperienceHolderPayload;
import com.direwolf20.justdirethings.common.network.data.ExperienceHolderSettingsPayload;
import com.direwolf20.justdirethings.util.ExperienceUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExperienceHolderScreen extends BaseMachineScreen<ExperienceHolderContainer> {
	private ExperienceHolderBE experienceHolderBE;
	private int targetExp;
	private boolean ownerOnly;
	private boolean collectExp;
	public boolean showParticles = true;

	public ExperienceHolderScreen(ExperienceHolderContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		if (container.baseMachineBE instanceof ExperienceHolderBE experienceHolderBE) {
			this.experienceHolderBE = experienceHolderBE;
			this.targetExp = experienceHolderBE.targetExp;
			this.ownerOnly = experienceHolderBE.ownerOnly;
			this.collectExp = experienceHolderBE.collectExp;
			this.showParticles = experienceHolderBE.showParticles;
		}
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(ToggleButtonFactory.STOREEXPBUTTON(topSectionLeft + (topSectionWidth / 2) + 15, topSectionTop + 62, true, b -> {
			int amt = 1;
			if (Screen.hasControlDown())
				amt = -1;
			else if (Screen.hasShiftDown())
				amt = amt * 10;
			PacketHandler.CHANNEL.sendToServer(new ExperienceHolderPayload(true, amt));
		}));
		addRenderableWidget(ToggleButtonFactory.EXTRACTEXPBUTTON(topSectionLeft + (topSectionWidth / 2) - 15 - 18, topSectionTop + 62, true, b -> {
			int amt = 1;
			if (Screen.hasControlDown())
				amt = -1;
			else if (Screen.hasShiftDown())
				amt = amt * 10;
			PacketHandler.CHANNEL.sendToServer(new ExperienceHolderPayload(false, amt));
		}));
		addRenderableWidget(ToggleButtonFactory.TARGETEXPBUTTON(topSectionLeft + (topSectionWidth / 2) - 15 - 42, topSectionTop + 64, targetExp, b -> {
			targetExp = ((NumberButton) b).getValue(); //The value is updated in the mouseClicked method below
			saveSettings();
		}));
		addRenderableWidget(ToggleButtonFactory.OWNERONLYBUTTON(topSectionLeft + (topSectionWidth / 2) - 15 - 60, topSectionTop + 62, ownerOnly, b -> {
			ownerOnly = !ownerOnly;
			((GrayscaleButton) b).toggleActive();
			saveSettings();
		}));
		addRenderableWidget(ToggleButtonFactory.COLLECTEXPBUTTON(topSectionLeft + (topSectionWidth / 2) + 15, topSectionTop + 42, collectExp, b -> {
			collectExp = !collectExp;
			((GrayscaleButton) b).toggleActive();
			saveSettings();
		}));
		addRenderableWidget(ToggleButtonFactory.SHOWPARTICLESBUTTON(topSectionLeft + (topSectionWidth / 2) + 31, topSectionTop + 42, showParticles, b -> {
			showParticles = !showParticles;
			((GrayscaleButton) b).toggleActive();
			saveSettings();
		}));
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		renderXPBar(poseStack, partialTicks, mouseX, mouseY);
	}

	public void renderXPBar(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		int barX = topSectionLeft + (topSectionWidth / 2) - (182 / 2);  // Position for the XP bar in your GUI
		int barY = topSectionTop + topSectionHeight - 15;  // Y position for the XP bar

		// Bind the vanilla experience bar texture (this is the same texture used by the player XP bar)
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		blit(poseStack, barX, barY, 0, 64, 182, 5);
		int partialAmount = (int) (ExperienceUtils.getProgressToNextLevel(experienceHolderBE.exp) * 183.0F);
		if (partialAmount > 0) {
			blit(poseStack, barX, barY, 0, 69, partialAmount, 5);
		}
		String s = String.valueOf(ExperienceUtils.getLevelFromTotalExperience(experienceHolderBE.exp));
		int j = topSectionLeft + (topSectionWidth / 2) - font.width(s) / 2;
		int k = topSectionTop + 62 + (font.lineHeight / 2);
		font.draw(poseStack, s, j + 1, k, 0);
		font.draw(poseStack, s, j - 1, k, 0);
		font.draw(poseStack, s, j, k + 1, 0);
		font.draw(poseStack, s, j, k - 1, 0);
		font.draw(poseStack, s, j, k, 8453920);
	}

	@Override
	public void saveSettings() {
		super.saveSettings();
		PacketHandler.CHANNEL.sendToServer(new ExperienceHolderSettingsPayload(targetExp, ownerOnly, collectExp, showParticles));
	}
}
