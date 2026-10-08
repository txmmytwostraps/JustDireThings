package com.direwolf20.justdirethings.client.screens.widgets;

import com.direwolf20.justdirethings.client.screens.basescreens.SensorScreenInterface;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class BlockStateScrollList extends ObjectSelectionList<BlockStateScrollList.BlockStateEntry> {
	private static String stripControlCodes(String value) {
		return net.minecraft.util.StringUtil.stripColor(value);
	}

	private final int listWidth;
	private ItemStack stateStack = ItemStack.EMPTY;
	private SensorScreenInterface parent;
	private boolean keyboardFocused;

	public BlockStateScrollList(SensorScreenInterface parent, int left, int listWidth, int top, int bottom) {
		super(Minecraft.getInstance(), listWidth, bottom - top, top, bottom, parent.getFontRenderer().lineHeight * 2 + 8);
		this.parent = parent;
		this.listWidth = listWidth;
		this.setRenderBackground(false);
		this.setRenderTopAndBottom(false);
		this.refreshList();
		setLeftPos(left);
	}

	public ItemStack getStateStack() {
		return stateStack;
	}

	public void setStateStack(ItemStack stack) {
		this.stateStack = stack;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.x0 + this.listWidth - 5;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	public void refreshList() {
		this.clearEntries();
		this.setSelected(null); // clearEntries doesn't reset it
		if (stateStack.getItem() instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			BlockState defaultState = block.defaultBlockState();
			for (Property<?> property : defaultState.getProperties()) {
				List<Comparable<?>> values = new ArrayList<>(property.getPossibleValues());
				Comparable<?> setValue = parent.getValue(property);
				boolean isAny = false;
				if (setValue == null) {
					setValue = defaultState.getValue(property);
					isAny = true;
				}
				addEntry(new BlockStateEntry(property, setValue, defaultState.getValue(property), parent, values, isAny));
			}
		} else if (stateStack.getItem() instanceof BucketItem bucketItem) {
			BlockState defaultState = bucketItem.getFluid().defaultFluidState().createLegacyBlock();
			for (Property<?> property : defaultState.getProperties()) {
				List<Comparable<?>> values = new ArrayList<>(property.getPossibleValues());
				Comparable<?> setValue = parent.getValue(property);
				boolean isAny = false;
				if (setValue == null) {
					setValue = defaultState.getValue(property);
					isAny = true;
				}
				addEntry(new BlockStateEntry(property, setValue, defaultState.getValue(property), parent, values, isAny));
			}
		}
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		renderContentBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	protected void renderContentBackground(PoseStack poseStack) {
		GuiUtils.drawGradientRect(poseStack.last().pose(), 0, this.x0, this.y0, this.x1, this.y1, 0xC0101010, 0xD0101010);
	}

	@Override
	protected void renderBackground(PoseStack poseStack) {
		// Dims the world behind the list
		GuiComponent.fill(poseStack, this.x0, this.y0, this.x1, this.y1, 0x70000000);
	}

	@Override
	protected void renderList(PoseStack poseStack, int rowLeft, int scrollTop, int mouseX, int mouseY, float partialTicks) {
		// Clip rows scrolled past the edges
		Window window = this.minecraft.getWindow();
		double scale = window.getGuiScale();
		RenderSystem.enableScissor((int) (this.x0 * scale), (int) (window.getHeight() - this.y1 * scale), (int) (this.width * scale), (int) (this.height * scale));
		super.renderList(poseStack, rowLeft, scrollTop, mouseX, mouseY, partialTicks);
		RenderSystem.disableScissor();
		// Separator lines above and below
		GuiComponent.fill(poseStack, this.x0, this.y0 - 2, this.x1, this.y0 - 1, 0x33FFFFFF);
		GuiComponent.fill(poseStack, this.x0, this.y0 - 1, this.x1, this.y0, 0xBF000000);
		GuiComponent.fill(poseStack, this.x0, this.y1, this.x1, this.y1 + 1, 0xBF000000);
		GuiComponent.fill(poseStack, this.x0, this.y1 + 1, this.x1, this.y1 + 2, 0x33FFFFFF);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0)
			return false;
		this.updateScrollingState(mouseX, mouseY, button);
		if (!this.isMouseOver(mouseX, mouseY))
			return false;
		// Rows run under the scrollbar, so look them up at the middle
		BlockStateEntry entry = this.getEntryAtPosition(this.x0 + this.width / 2, mouseY);
		if (entry != null && entry.mouseClicked(mouseX, mouseY, button)) {
			this.setFocused(entry);
			this.setDragging(true);
			return true;
		}
		return mouseX >= this.getScrollbarPosition() && mouseX < this.getScrollbarPosition() + 6;
	}

	@Override
	public boolean changeFocus(boolean focus) {
		keyboardFocused = super.changeFocus(focus);
		return keyboardFocused;
	}

	// White selection outline only with keyboard focus
	@Override
	protected boolean isFocused() {
		return keyboardFocused;
	}

	public class BlockStateEntry extends ObjectSelectionList.Entry<BlockStateEntry> {
		private final Property<?> property;
		private final SensorScreenInterface parent;
		private Comparable<?> currentValue;
		private final List<Comparable<?>> possibleValues;
		private final int anyIndex;
		private boolean isAny;

		BlockStateEntry(Property<?> property, Comparable<?> currentValue, Comparable<?> defaultValue, SensorScreenInterface parent, List<Comparable<?>> possibleValues, boolean isAny) {
			this.property = property;
			this.currentValue = currentValue;
			this.anyIndex = possibleValues.indexOf(defaultValue);
			this.parent = parent;
			this.possibleValues = possibleValues;
			this.isAny = isAny;
		}

		@Override
		public Component getNarration() {
			return new TranslatableComponent("narrator.select", property.getName());
		}

		@Override
		public void render(PoseStack poseStack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
			Component name = new TextComponent(stripControlCodes(property.getName()));
			Component value = new TextComponent(stripControlCodes(isAny ? "ANY" : this.currentValue.toString()));
			Font font = this.parent.getFontRenderer();
			// The selection box leaves a black shader color behind
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			font.draw(poseStack, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, listWidth))), left + 3, top + 2, 0xFFFFFF);
			font.draw(poseStack, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(value, listWidth))), left + 3, top + 2 + font.lineHeight, 0xCCCCCC);
		}

		@Override
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			BlockStateScrollList.this.setSelected(this);
			int currentIndex = possibleValues.indexOf(this.currentValue);
			int nextIndex;
			if (currentIndex == anyIndex && isAny) {
				nextIndex = anyIndex;
				isAny = false;
			} else {
				nextIndex = (currentIndex + 1) % possibleValues.size();
				if (nextIndex == anyIndex) isAny = true;
			}
			currentValue = possibleValues.get(nextIndex);
			parent.setPropertyValue(property, currentValue, isAny);

			return false;
		}
	}
}
