package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.screens.basescreens.BaseMachineScreen;
import com.direwolf20.justdirethings.client.screens.standardbuttons.ToggleButtonFactory;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.client.screens.widgets.ToggleButton;
import com.direwolf20.justdirethings.common.blockentities.ParadoxMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.FluidMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.PoweredMachineBE;
import com.direwolf20.justdirethings.common.containers.ParadoxMachineContainer;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.ParadoxMachineSnapshotPayload;
import com.direwolf20.justdirethings.common.network.data.ParadoxRenderPayload;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.MiscTools;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;
import java.util.Arrays;

public class ParadoxMachineScreen extends BaseMachineScreen<ParadoxMachineContainer> {
	protected static final ResourceLocation PARADOXBAR = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/paradoxbar.png");
	private boolean renderParadox = false;
	private int targetType = 0;
	public ParadoxMachineScreen(ParadoxMachineContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		if (container.baseMachineBE instanceof ParadoxMachineBE paradoxMachineBE) {
			renderParadox = paradoxMachineBE.renderParadox;
			targetType = paradoxMachineBE.targetType;
		}
	}

	@Override
	public void init() {
		super.init();
		addSnapshotButton();
		addRenderButton();
		addTargetButton();
	}

	@Override
	public void setTopSection() {
		extraWidth = 80;
		extraHeight = 0;
	}

	public void addSnapshotButton() {
		addRenderableWidget(ToggleButtonFactory.SNAPSHOT_AREA_BUTTON(getGuiLeft() + 116, topSectionTop + 62, b -> {
			PacketHandler.CHANNEL.sendToServer(new ParadoxMachineSnapshotPayload());
		}));
	}

	public void addRenderButton() {
		addRenderableWidget(ToggleButtonFactory.RENDERPARADOXBUTTON(getGuiLeft() + 98, topSectionTop + 62, renderParadox, b -> {
			renderParadox = !renderParadox;
			((GrayscaleButton) b).toggleActive();
			saveSettings();
		}));
	}

	public void addTargetButton() {
		addRenderableWidget(ToggleButtonFactory.PARADOXTARGETBUTTON(getGuiLeft() + 56, topSectionTop + 38, targetType, b -> {
			targetType = ((ToggleButton) b).getTexturePosition();
			saveSettings();
		}));
	}

	@Override
	public void saveSettings() {
		super.saveSettings();
		PacketHandler.CHANNEL.sendToServer(new ParadoxRenderPayload(renderParadox, targetType));
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		if (baseMachineBE instanceof ParadoxMachineBE paradoxMachineBE) {
			RenderSystem.setShaderTexture(0, PARADOXBAR);
			blit(poseStack, topSectionLeft + topSectionWidth - 18 - 5, topSectionTop + 5, 0, 0, 18, 72, 36, 72);
			float maxEnergy = paradoxMachineBE.getMaxParadoxEnergy(), height = 70;
			if (maxEnergy > 0 && paradoxMachineBE.paradoxEnergy > 0) {
				float remaining = (paradoxMachineBE.paradoxEnergy * height) / maxEnergy;
				// Calculate the color based on time for a rainbow effect
				long time = System.currentTimeMillis();
				int color = Color.HSBtoRGB((time % 10800L) / 10800.0f, 1.0f, 1.0f); // Adjust HSB values for different effects

				int red = (color >> 16) & 0xFF;
				int green = (color >> 8) & 0xFF;
				int blue = color & 0xFF;

				// Apply the rainbow color effect
				RenderSystem.setShaderColor(red / 255.0f, green / 255.0f, blue / 255.0f, 1.0f); // Normalize RGB values to 0-1 range
				blit(poseStack, topSectionLeft + topSectionWidth - 18 - 5 + 1, topSectionTop + getEnergyBarOffset() + 72 - 2 - (int) remaining, 19, 69 - remaining, 17, (int) remaining + 1, 36, 72);
			}
		}
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
		super.renderTooltip(poseStack, pX, pY);
		paradoxBarTooltip(poseStack, pX, pY);
	}

	public void paradoxBarTooltip(PoseStack poseStack, int pX, int pY) {
		if (baseMachineBE instanceof ParadoxMachineBE paradoxMachineBE) {
			if (MiscTools.inBounds(topSectionLeft + topSectionWidth - 18 - 5, topSectionTop + 5, 18, 72, pX, pY)) {
				String energyFormatted = String.format("%.2f", paradoxMachineBE.paradoxEnergy);

				renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(
						new TranslatableComponent("justdirethings.paradoxenergy", energyFormatted, paradoxMachineBE.getMaxParadoxEnergy())
				)), pX, pY);
			}
		}
	}

	@Override
	public void powerBarTooltip(PoseStack poseStack, int pX, int pY) {
		if (baseMachineBE instanceof ParadoxMachineBE paradoxMachineBE && baseMachineBE instanceof PoweredMachineBE poweredMachineBE) {
			if (MiscTools.inBounds(topSectionLeft + 5, topSectionTop + 5, 18, 72, pX, pY)) {
				int totalBlocks = paradoxMachineBE.testRestoreBlocks().size();
				int totalEntities = paradoxMachineBE.getEntitiesFromNBT().size();
				int totalCostEnergy = paradoxMachineBE.getEnergyCost(totalBlocks, totalEntities);
				if (hasShiftDown())
					renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(
							new TranslatableComponent("justdirethings.screen.energy", MagicHelpers.formatted(this.container.getEnergy()), MagicHelpers.formatted(poweredMachineBE.getMaxEnergy())),
							new TranslatableComponent("justdirethings.screen.paradoxenergycost", MagicHelpers.formatted(totalCostEnergy))
					)), pX, pY);
				else
					renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(
							new TranslatableComponent("justdirethings.screen.energy", MagicHelpers.withSuffix(this.container.getEnergy()), MagicHelpers.withSuffix(poweredMachineBE.getMaxEnergy())),
							new TranslatableComponent("justdirethings.screen.paradoxenergycost", MagicHelpers.withSuffix(totalCostEnergy))
					)), pX, pY);
			}
		}
	}

	@Override
	public void fluidBarTooltip(PoseStack poseStack, int pX, int pY) {
		if (baseMachineBE instanceof ParadoxMachineBE paradoxMachineBE && baseMachineBE instanceof FluidMachineBE fluidMachineBE) {
			if (MiscTools.inBounds(topSectionLeft + getFluidBarOffset(), topSectionTop + 5, 18, 72, pX, pY)) {
				int totalBlocks = paradoxMachineBE.testRestoreBlocks().size();
				int totalEntities = paradoxMachineBE.getEntitiesFromNBT().size();
				int totalCostFluid = paradoxMachineBE.getFluidCost(totalBlocks, totalEntities);
				if (hasShiftDown())
					renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(
							new TranslatableComponent("justdirethings.screen.fluid", this.container.getFluidStack().getDisplayName(), MagicHelpers.formatted(this.container.getFluidAmount()), MagicHelpers.formatted(fluidMachineBE.getMaxMB())),
							new TranslatableComponent("justdirethings.screen.paradoxfluidcost", MagicHelpers.formatted(totalCostFluid))
					)), pX, pY);
				else
					renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(
							new TranslatableComponent("justdirethings.screen.fluid", this.container.getFluidStack().getDisplayName(), MagicHelpers.withSuffix(this.container.getFluidAmount()), MagicHelpers.withSuffix(fluidMachineBE.getMaxMB())),
							new TranslatableComponent("justdirethings.screen.paradoxfluidcost", MagicHelpers.withSuffix(totalCostFluid))
					)), pX, pY);
			}
		}
	}
}
