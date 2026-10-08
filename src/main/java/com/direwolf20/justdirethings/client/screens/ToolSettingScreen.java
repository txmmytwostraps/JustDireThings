package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.screens.standardbuttons.ToggleButtonFactory;
import com.direwolf20.justdirethings.client.screens.widgets.BaseButton;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.client.screens.widgets.ToggleButton;
import com.direwolf20.justdirethings.common.containers.ToolSettingContainer;
import com.direwolf20.justdirethings.common.items.interfaces.*;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.ToggleToolLeftRightClickPayload;
import com.direwolf20.justdirethings.common.network.data.ToggleToolRefreshSlots;
import com.direwolf20.justdirethings.common.network.data.ToggleToolSlotPayload;
import com.direwolf20.justdirethings.util.MiscTools;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class ToolSettingScreen extends AbstractContainerScreen<ToolSettingContainer> {
	private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/settings.png");
	protected final ResourceLocation JUSTSLOT = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "textures/gui/justslot.png");

	protected final ToolSettingContainer container;
	Player player;
	protected ItemStack tool = ItemStack.EMPTY;
	private EnumSet<Ability> abilities = EnumSet.noneOf(Ability.class);
	int buttonsStartX = getGuiLeft() + 5;
	int buttonsStartY = getGuiTop() + 15;
	int toolSlot;
	protected Button shownAbilityButton;
	protected final Map<Button, ForgeSlider> sliders = new HashMap<>();
	protected final Map<Button, ToggleButton> leftRightClickButtons = new HashMap<>();
	protected final Map<Button, GrayscaleButton> bindingButtons = new HashMap<>();
	protected final Map<Button, ToggleButton> customSettingsButtons = new HashMap<>();
	protected final Map<Button, ToggleButton> requireEquippedButtons = new HashMap<>();
	protected final Map<Button, Ability> buttonToAbilityMap = new HashMap<>();
	protected Map<ToggleButton, ToolRecords.AbilityBinding> bindingMap = new HashMap<>();
	protected boolean bindingEnabled = false;
	protected boolean requireEquipped = true;
	protected Set<AbstractWidget> widgetsToRemove = new HashSet<>();
	protected Set<AbstractWidget> widgetsToAdd = new HashSet<>();
	protected boolean renderablesChanged = false;

	public ToolSettingScreen(ToolSettingContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		this.container = container;
		this.player = container.playerEntity;
		if (player.getMainHandItem().getItem() instanceof ToggleableTool) {
			tool = player.getMainHandItem();
			toolSlot = player.getInventory().selected;
		} else if (player.getOffhandItem().getItem() instanceof ToggleableTool) {
			tool = player.getOffhandItem();
			toolSlot = 40;
		}
	}

	public void clearMaps() {
		sliders.clear();
		leftRightClickButtons.clear();
		bindingButtons.clear();
		customSettingsButtons.clear();
		buttonToAbilityMap.clear();
		bindingMap.clear();
		bindingEnabled = false;
		shownAbilityButton = null;
	}

	public void refreshButtons() {
		buttonsStartX = getGuiLeft() + 5;
		buttonsStartY = getGuiTop() + 25;
		clearWidgets();
		clearMaps();
		int counter = 0;
		for (Ability toolAbility : abilities) {
			if (!ToggleableTool.hasUpgrade(tool, toolAbility))
				continue;
			Button button = null;
			if (toolAbility.getSettingType() == Ability.SettingType.TOGGLE) {
				boolean isActive = ToggleableTool.getSetting(tool, toolAbility.getName());
				button = new GrayscaleButton(buttonsStartX + ((counter / 2) * 18), buttonsStartY + ((counter % 2) * 18), 16, 16, toolAbility.getIconLocation(), new TranslatableComponent(toolAbility.getLocalization()), isActive, (clicked) -> {
					toggleSetting(toolAbility.getName());
					((GrayscaleButton) clicked).toggleActive();
				});
				addRenderableWidget(button);
				counter++;
			} else if (toolAbility.getSettingType() == Ability.SettingType.CYCLE) {
				boolean isActive = ToggleableTool.getSetting(tool, toolAbility.getName());
				AbilityParams abilityParams = ((ToggleableTool) tool.getItem()).getAbilityParams(toolAbility);
				int possibleValues = ((abilityParams.maxSlider - abilityParams.minSlider) / abilityParams.increment) + 2;
				int currentValue = ToggleableTool.getToolValue(tool, toolAbility.getName());
				int buttonPosition = !isActive ? 0 : currentValue / abilityParams.increment;
				button = ToggleButtonFactory.ABILITYCYCLEBUTTON(buttonsStartX + ((counter / 2) * 18), buttonsStartY + ((counter % 2) * 18), toolAbility, buttonPosition, possibleValues, (clicked) -> {
					cycleSetting(toolAbility.getName());
				});
				addRenderableWidget(button);
				counter++;
			} else if (toolAbility.getSettingType() == Ability.SettingType.SLIDER) {
				boolean isActive = ToggleableTool.getSetting(tool, toolAbility.getName());
				button = new GrayscaleButton(buttonsStartX + ((counter / 2) * 18), buttonsStartY + ((counter % 2) * 18), 16, 16, toolAbility.getIconLocation(), new TranslatableComponent(toolAbility.getLocalization()), isActive, (clicked) -> {
					toggleSetting(toolAbility.getName());
					((GrayscaleButton) clicked).toggleActive();
				});
				addRenderableWidget(button);
				AbilityParams abilityParams = ((ToggleableTool) tool.getItem()).getAbilityParams(toolAbility);
				int currentValue = ToggleableTool.getToolValue(tool, toolAbility.getName());
				if (abilityParams.minSlider != abilityParams.maxSlider) {
					ForgeSlider slider = new AbilitySlider(buttonsStartX + 20, buttonsStartY - 18, 100, 15, new TranslatableComponent(toolAbility.getLocalization()).append(": "), TextComponent.EMPTY, abilityParams.minSlider, abilityParams.maxSlider, currentValue, true) {
						@Override
						protected void applyValue() {
							setSetting(toolAbility.getName(), this.getValueInt());
							super.applyValue();
						}
					};
					sliders.put(button, slider);
				}
				counter++;
			}
			if (button != null && tool.getItem() instanceof LeftClickableTool && toolAbility.isBindable()) {
				int currentValue = LeftClickableTool.getBindingMode(tool, toolAbility);
				ToggleButton toggleButton;
				if (toolAbility.getBindingType() == Ability.BindingType.LEFT_AND_CUSTOM) {
					toggleButton = ToggleButtonFactory.LEFTRIGHTCUSTOMCLICKBUTTON(buttonsStartX + 125, buttonsStartY - 18, currentValue, (clicked) -> {
						ToolRecords.AbilityBinding binding = bindingMap.get(((ToggleButton) clicked));
						if (binding == null) {
							sendBinding(toolAbility.getName(), ((ToggleButton) clicked).getTexturePosition(), -1, false);
							requireEquipped = true;
						} else {
							sendBinding(toolAbility.getName(), ((ToggleButton) clicked).getTexturePosition(), binding.key(), binding.isMouse());
							requireEquipped = binding.requireEquipped();
						}
						if (((ToggleButton) clicked).getTexturePosition() == 2) {
							if (!renderables.contains(bindingButtons.get(shownAbilityButton)))
								widgetsToAdd.add(bindingButtons.get(shownAbilityButton));
							if (!renderables.contains(requireEquippedButtons.get(shownAbilityButton)))
								widgetsToAdd.add(requireEquippedButtons.get(shownAbilityButton));
						} else {
							widgetsToRemove.add(bindingButtons.get(shownAbilityButton));
							widgetsToRemove.add(requireEquippedButtons.get(shownAbilityButton));
						}
						renderablesChanged = true;
					});
					leftRightClickButtons.put(button, toggleButton);
				} else {
					toggleButton = ToggleButtonFactory.CUSTOMCLICKBUTTON(buttonsStartX + 125, buttonsStartY - 18, 0, (clicked) -> {
						ToolRecords.AbilityBinding binding = bindingMap.get(((ToggleButton) clicked));
						if (binding == null) {
							sendBinding(toolAbility.getName(), 2, -1, false); //Button Type 2 hardcoded to custom
							requireEquipped = true;
						} else {
							sendBinding(toolAbility.getName(), 2, binding.key(), binding.isMouse());
							requireEquipped = binding.requireEquipped();
						}
						if (!renderables.contains(bindingButtons.get(shownAbilityButton)))
							widgetsToAdd.add(bindingButtons.get(shownAbilityButton));
						if (!renderables.contains(requireEquippedButtons.get(shownAbilityButton)))
							widgetsToAdd.add(requireEquippedButtons.get(shownAbilityButton));
						renderablesChanged = true;
					});
					leftRightClickButtons.put(button, toggleButton);
				}

				GrayscaleButton bindingButton = ToggleButtonFactory.KEYBIND_BUTTON(buttonsStartX + 143, buttonsStartY - 18, false, (clicked) -> {
					bindingEnabled = true;
					((GrayscaleButton) clicked).toggleActive();
				});
				bindingMap.put(toggleButton, LeftClickableTool.getAbilityBinding(tool, toolAbility));
				this.bindingButtons.put(button, bindingButton);

				requireEquipped = bindingMap.get(toggleButton) == null ? true : bindingMap.get(toggleButton).requireEquipped();
				ToggleButton requireEquippedButton = ToggleButtonFactory.REQUIRE_EQUIPPED_BUTTON(buttonsStartX + 125, buttonsStartY, requireEquipped ? 0 : 1, (clicked2) -> {
					requireEquipped = !requireEquipped;
					ToolRecords.AbilityBinding binding = bindingMap.get(toggleButton);
					if (binding == null)
						sendBinding(toolAbility.getName(), 2, -1, false);
					else
						sendBinding(toolAbility.getName(), 2, binding.key(), binding.isMouse());
				});
				this.requireEquippedButtons.put(button, requireEquippedButton);
			}
			if (button != null && toolAbility.hasCustomSetting()) {
				Ability.CustomSettingType customSettingType = toolAbility.getCustomSetting();
				int value = ToggleableTool.getCustomSetting(tool, toolAbility.getName());
				if (customSettingType == Ability.CustomSettingType.RENDER) {
					ToggleButton hideRenderButton = ToggleButtonFactory.HIDE_RENDER_ABILITY_BUTTON(buttonsStartX + 143, buttonsStartY, value, (clicked) -> {
						toggleRender(toolAbility.getName(), ((ToggleButton) clicked).getTexturePosition());
					});
					this.customSettingsButtons.put(button, hideRenderButton);
				} else if (customSettingType == Ability.CustomSettingType.TARGET) {
					ToggleButton targetButton = ToggleButtonFactory.HOMING_TARGET_BUTTON(buttonsStartX + 143, buttonsStartY, value, (clicked) -> {
						toggleRender(toolAbility.getName(), ((ToggleButton) clicked).getTexturePosition());
					});
					this.customSettingsButtons.put(button, targetButton);
				}
			}
			if (button != null)
				buttonToAbilityMap.put(button, toolAbility);
		}
	}

	protected void sendBinding(String abilityName, int buttonType, int keyCode, boolean isMouse) {
		PacketHandler.CHANNEL.sendToServer(new ToggleToolLeftRightClickPayload(toolSlot, abilityName, buttonType, keyCode, isMouse, requireEquipped));
	}

	protected void collectButtonsToRemove() {
		widgetsToRemove.addAll(sliders.values());
		widgetsToRemove.addAll(leftRightClickButtons.values());
		widgetsToRemove.addAll(bindingButtons.values());
		widgetsToRemove.addAll(customSettingsButtons.values());
		widgetsToRemove.addAll(requireEquippedButtons.values());
	}

	public void toggleSetting(String settingName) {
		PacketHandler.CHANNEL.sendToServer(new ToggleToolSlotPayload(settingName, toolSlot, 0, -1));
	}

	public void cycleSetting(String settingName) {
		PacketHandler.CHANNEL.sendToServer(new ToggleToolSlotPayload(settingName, toolSlot, 1, -1));
	}

	public void setSetting(String settingName, int value) {
		PacketHandler.CHANNEL.sendToServer(new ToggleToolSlotPayload(settingName, toolSlot, 2, value));
	}

	public void toggleRender(String settingName, int value) {
		PacketHandler.CHANNEL.sendToServer(new ToggleToolSlotPayload(settingName, toolSlot, 3, value));
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
		if (!tool.isEmpty()) {
			int x = getGuiLeft() + 5;
			int y = getGuiTop() + 5;
			int j1 = x + y * this.imageWidth;
			this.itemRenderer.renderAndDecorateItem(null, tool, x, y, j1);
			this.itemRenderer.renderGuiItemDecorations(this.font, tool, x, y);
		}
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int pX, int pY) {
		super.renderTooltip(poseStack, pX, pY);
		for (Widget renderable : this.renderables) {
			if (renderable instanceof ToggleButton button && showCustomBinding() && !button.getLocalization(pX, pY).equals(TextComponent.EMPTY) && !requireEquippedButtons.containsValue(button) && !customSettingsButtons.containsValue(button)) { //2 is custom
				if (bindingMap.get(button) == null) {
					renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(button.getLocalization(), new TranslatableComponent("justdirethings.unbound-screen"))), pX, pY);
				} else {
					ToolRecords.AbilityBinding binding = bindingMap.get(button);
					if (binding.isMouse()) {
						renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(button.getLocalization(), new TranslatableComponent("justdirethings.bound-mouse", binding.key()))), pX, pY);
					} else {
						String bindingName = InputConstants.getKey(binding.key(), 0).getDisplayName().getString();
						renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(button.getLocalization(), new TranslatableComponent("justdirethings.bound-key", bindingName))), pX, pY);
					}
				}
			} else if (renderable instanceof BaseButton button && !button.getLocalization(pX, pY).equals(TextComponent.EMPTY)) {
				if (sliders.containsKey(button) || leftRightClickButtons.containsKey(button))
					renderTooltip(poseStack, Language.getInstance().getVisualOrder(Arrays.asList(button.getLocalization(), new TranslatableComponent("justdirethings.screen.rightclicksettings"))), pX, pY);
				else
					renderTooltip(poseStack, button.getLocalization(), pX, pY);
			}
		}
	}

	@Override
	public void init() {
		super.init();
		if (this.tool.getItem() instanceof ToggleableTool toggleableTool) {
			this.abilities = toggleableTool.getAbilities();
			refreshButtons();
			refreshToolAndSlots();
		}
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
		if (renderablesChanged)
			updateRenderables();
		RenderSystem.setShaderTexture(0, JUSTSLOT);
		for (Slot slot : container.dynamicSlots) {
			blit(poseStack, getGuiLeft() + slot.x - 1, getGuiTop() + slot.y - 1, 0, 0, 18, 18);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (shownAbilityButton != null && leftRightClickButtons.get(shownAbilityButton) != null && bindingButtons.get(shownAbilityButton) != null && this.bindingEnabled) {
			if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
				bindingMap.put(leftRightClickButtons.get(shownAbilityButton), null);
				leftRightClickButtons.get(shownAbilityButton).onPress(); //This fires the packet to the server
				bindingButtons.get(shownAbilityButton).toggleActive();
				this.bindingEnabled = false;
				return true;
			} else {
				bindingMap.put(leftRightClickButtons.get(shownAbilityButton), new ToolRecords.AbilityBinding(buttonToAbilityMap.get(shownAbilityButton).getName(), pKeyCode, false, requireEquipped));
				leftRightClickButtons.get(shownAbilityButton).onPress(); //This fires the packet to the server
				bindingButtons.get(shownAbilityButton).toggleActive();
				this.bindingEnabled = false;
				return true;
			}
		}
		InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
		if (pKeyCode == 256 || minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
			onClose();

			return true;
		}

		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}

	@Override
	public boolean mouseClicked(double x, double y, int btn) {
		if (btn != 0 && btn != 1 && shownAbilityButton != null && leftRightClickButtons.get(shownAbilityButton) != null && bindingButtons.get(shownAbilityButton) != null && this.bindingEnabled) {
			bindingMap.put(leftRightClickButtons.get(shownAbilityButton), new ToolRecords.AbilityBinding(buttonToAbilityMap.get(shownAbilityButton).getName(), btn, true, requireEquipped));
			leftRightClickButtons.get(shownAbilityButton).onPress();
			bindingButtons.get(shownAbilityButton).toggleActive();
			this.bindingEnabled = false;
			return true;
		}
		if (hoveredSlot != null && hoveredSlot.getItem().getItem() instanceof ToggleableTool toggleableTool) {
			tool = hoveredSlot.getItem();
			toolSlot = hoveredSlot.getSlotIndex();
			this.abilities = toggleableTool.getAbilities();
			refreshButtons();
			refreshToolAndSlots();
			return true;
		}
		if (btn == 1) {
			for (Widget renderable : new ArrayList<>(renderables)) {  // Create a copy of renderables to iterate over
				if (renderable instanceof Button button && (sliders.containsKey(button) || leftRightClickButtons.containsKey(button) || bindingButtons.containsKey(button) || customSettingsButtons.containsKey(button)) && MiscTools.inBounds(button.x, button.y, button.getWidth(), button.getHeight(), x, y)) { //If right click on any button, clear the optional buttons first.
					collectButtonsToRemove();
					if (button.equals(shownAbilityButton))
						shownAbilityButton = null;
					else
						shownAbilityButton = button;
					if (sliders.containsKey(shownAbilityButton)) {
						widgetsToAdd.add(sliders.get(shownAbilityButton));
					}
					if (leftRightClickButtons.containsKey(shownAbilityButton)) {
						widgetsToAdd.add(leftRightClickButtons.get(shownAbilityButton));
					}
					if (bindingButtons.containsKey(shownAbilityButton) && showCustomBinding()) {
						widgetsToAdd.add(bindingButtons.get(shownAbilityButton));
					}
					if (requireEquippedButtons.containsKey(shownAbilityButton) && showCustomBinding()) {
						widgetsToAdd.add(requireEquippedButtons.get(shownAbilityButton));
						requireEquipped = bindingMap.get(leftRightClickButtons.get(shownAbilityButton)) == null ? true : bindingMap.get(leftRightClickButtons.get(shownAbilityButton)).requireEquipped();
					}
					if (customSettingsButtons.containsKey(shownAbilityButton)) {
						widgetsToAdd.add(customSettingsButtons.get(shownAbilityButton));
					}
					button.playDownSound(Minecraft.getInstance().getSoundManager());
				}
			}

			if (widgetsToRemove.size() > 0 || widgetsToAdd.size() > 0) {
				renderablesChanged = true;
				return true;
			}
		}
		return super.mouseClicked(x, y, btn);
	}

	public boolean showCustomBinding() {
		return (shownAbilityButton != null & leftRightClickButtons.containsKey(shownAbilityButton) && (leftRightClickButtons.get(shownAbilityButton).getTexturePosition() == 2 || (leftRightClickButtons.get(shownAbilityButton).getTexturePosition() == 0 && buttonToAbilityMap.get(shownAbilityButton).getBindingType() == Ability.BindingType.CUSTOM_ONLY)));
	}

	public void refreshToolAndSlots() {
		this.container.refreshSlots(tool);
		PacketHandler.CHANNEL.sendToServer(new ToggleToolRefreshSlots(toolSlot));
	}

	public void updateRenderables() {
		if (!widgetsToRemove.isEmpty()) {
			for (AbstractWidget abstractWidget : widgetsToRemove) {
				removeWidget(abstractWidget);
			}
			widgetsToRemove.clear();
		}
		if (!widgetsToAdd.isEmpty()) {
			for (AbstractWidget abstractWidget : widgetsToAdd) {
				addRenderableWidget(abstractWidget);
			}
			widgetsToAdd.clear();
		}
		renderablesChanged = false;
	}

	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		if (pButton == 0 && this.isDragging()) {
			this.setDragging(false);
			if (this.getFocused() != null) {
				return this.getFocused().mouseReleased(pMouseX, pMouseY, pButton);
			}
			return this.getChildAt(pMouseX, pMouseY).filter(p_94708_ -> p_94708_.mouseReleased(pMouseX, pMouseY, pButton)).isPresent();
		}
		return super.mouseReleased(pMouseX, pMouseY, pButton);

	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		if (pButton == 0 && this.isDragging()) {
			return this.getFocused() != null && this.isDragging() && pButton == 0
					? this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
					: false;
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		this.sliders.forEach((button, slider) -> {
			if (slider.isMouseOver(mouseX, mouseY)) {
				slider.setValue(slider.getValueInt() + (delta > 0 ? 1 : -1));
			}
		});

		return false;
	}

	/**
	 * The ability value slider. Vanilla's textures are 20 pixels tall, so they're cut down to fit.
	 * Scroll and arrow key changes reach the server like drags.
	 */
	private class AbilitySlider extends ForgeSlider {
		AbilitySlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
			super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
		}

		@Override
		public void setValue(double value) {
			double oldValue = this.value;
			super.setValue(value);
			if (!Mth.equal(oldValue, this.value))
				this.applyValue();
		}

		@Override
		public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
			RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			blitRows(poseStack, this.x, 0, 46, this.width - 1, 1);
			blitRows(poseStack, this.x + this.width - 1, 199, 46, 1, 1);
			int handleX = this.x + (int) (this.value * (double) (this.width - 8));
			int handleV = 46 + (this.isHovered || ToolSettingScreen.this.getFocused() == this ? 2 : 1) * 20;
			blitRows(poseStack, handleX, 0, handleV, 4, 3);
			blitRows(poseStack, handleX + 4, 196, handleV, 4, 3);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			renderLabel(poseStack, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
		}

		private void blitRows(PoseStack poseStack, int x, int u, int v, int width, int bottom) {
			blit(poseStack, x, this.y, u, v, width, this.height - bottom);
			blit(poseStack, x, this.y + this.height - bottom, u, v + 20 - bottom, width, bottom);
		}

		private void renderLabel(PoseStack poseStack, int color) {
			Font font = Minecraft.getInstance().font;
			Component label = this.getMessage();
			int minX = this.x + 2, maxX = this.x + this.width - 2;
			int minY = this.y, maxY = this.y + this.height;
			int textY = (minY + maxY - 9) / 2 + 1;
			int overflow = font.width(label) - (maxX - minX);
			if (overflow > 0) {
				double period = Math.max((double) overflow * 0.5, 3.0);
				double progress = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * ((double) Util.getMillis() / 1000.0) / period)) / 2.0 + 0.5;
				double offset = Mth.lerp(progress, 0.0, (double) overflow);
				Window window = Minecraft.getInstance().getWindow();
				double scale = window.getGuiScale();
				RenderSystem.enableScissor((int) (minX * scale), (int) (window.getHeight() - maxY * scale), (int) ((maxX - minX) * scale), (int) ((maxY - minY) * scale));
				drawString(poseStack, font, label, minX - (int) offset, textY, color);
				RenderSystem.disableScissor();
			} else {
				drawCenteredString(poseStack, font, label, (minX + maxX) / 2, textY, color);
			}
		}
	}
}
