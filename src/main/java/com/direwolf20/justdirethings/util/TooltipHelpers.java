package com.direwolf20.justdirethings.util;

import com.direwolf20.justdirethings.client.KeyBindings;
import com.direwolf20.justdirethings.common.capabilities.ItemStackItemHandler;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.List;

import static com.direwolf20.justdirethings.common.items.PocketGenerator.COUNTER;
import static com.direwolf20.justdirethings.common.items.PocketGenerator.FUEL_INVENTORY;
import static com.direwolf20.justdirethings.common.items.PocketGenerator.MAXBURN;

public class TooltipHelpers {

	public static void appendFEText(ItemStack stack, List<Component> tooltip) {
		if (!(stack.getItem() instanceof PoweredItem poweredItem))
			return;

		var energy = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
		if (energy == null) {
			return;
		}
		if (Screen.hasShiftDown())
			tooltip.add(
					new TranslatableComponent("justdirethings.festored", MagicHelpers.formatted(energy.getEnergyStored()),
									MagicHelpers.formatted(energy.getMaxEnergyStored()))
							.withStyle(ChatFormatting.GREEN));
		else
			tooltip.add(
					new TranslatableComponent("justdirethings.festored", MagicHelpers.tidyValue(energy.getEnergyStored()),
									MagicHelpers.tidyValue(energy.getMaxEnergyStored()))
							.withStyle(ChatFormatting.GREEN));
	}

	public static void appendToolEnabled(ItemStack stack, List<Component> tooltip) {
		if (stack.getItem() instanceof ToggleableItem toggleableItem) {
			if (toggleableItem.getEnabled(stack))
				tooltip.add(
						new TranslatableComponent("justdirethings.enabled").withStyle(
										ChatFormatting.GREEN)
								.append(new TextComponent(
												" ")
										.append(new TranslatableComponent("justdirethings.presshotkey",
														KeyBindings.toggleTool.getKey().getDisplayName())
												.withStyle(ChatFormatting.DARK_GRAY))));
			else
				tooltip.add(
						new TranslatableComponent("justdirethings.disabled").withStyle(
										ChatFormatting.DARK_RED)
								.append(new TextComponent(
												" ")
										.append(new TranslatableComponent("justdirethings.presshotkey",
														KeyBindings.toggleTool.getKey().getDisplayName())
												.withStyle(ChatFormatting.DARK_GRAY))));
		}
	}

	public static void appendAbilityList(ItemStack stack, List<Component> tooltip) {
		if (stack.getItem() instanceof ToggleableTool toggleableTool) {
			NBTHelpers.BoundInventory boundInventory = ToggleableTool.getBoundInventory(stack);
			for (Ability ability : toggleableTool.getAbilities()) {
				boolean active = ToggleableTool.getSetting(stack, ability.getName());
				ChatFormatting chatFormatting = ChatFormatting.GRAY;
				if (!ToggleableTool.hasUpgrade(stack, ability)) {
					tooltip.add(new TranslatableComponent(ability.getLocalization())
							.append(new TranslatableComponent("justdirethings.missingupgrade")).withStyle(chatFormatting));
				} else {
					chatFormatting = active ? ChatFormatting.GREEN : ChatFormatting.DARK_RED;
					tooltip.add(new TranslatableComponent(ability.getLocalization()).withStyle(chatFormatting));
					if (ability.equals(Ability.DROPTELEPORT)) {
						chatFormatting = ChatFormatting.DARK_PURPLE;
						String dimString;
						if (boundInventory == null) {
							dimString = I18n.get("justdirethings.unbound");
							tooltip.add(new TextComponent(dimString).withStyle(chatFormatting));
						} else {
							dimString = " -" + I18n.get(boundInventory.globalPos().dimension().location().getPath()) + ": [" + boundInventory.globalPos().pos().toShortString() + "]";
							tooltip.add(new TextComponent(dimString).withStyle(chatFormatting));
							tooltip.add(new TextComponent("").append(new TranslatableComponent("justdirethings.boundside"))
									.append(new TranslatableComponent(
											"justdirethings.screen.direction-" + boundInventory.direction().getName()))
									.withStyle(chatFormatting));
						}
					}
				}
			}
		}
	}

	public static void appendShiftForInfo(ItemStack stack, List<Component> tooltip) {
		tooltip.add(new TranslatableComponent("justdirethings.shiftmoreinfo").withStyle(ChatFormatting.GRAY));
	}

	public static void appendUpgradeDetails(ItemStack stack, List<Component> tooltip) {
		Ability ability = Ability.getAbilityFromUpgradeItem(stack.getItem());
		if (ability == null) return;

		String detailTextKey = "justdirethings." + ability.getName() + ".detailtext";
		String flavorTextKey = "justdirethings." + ability.getName() + ".flavortext";

		MutableComponent detailTextComponent = new TranslatableComponent(detailTextKey);
		if (!detailTextComponent.getString().equals(detailTextKey)) {
			tooltip.add(detailTextComponent.withStyle(ChatFormatting.GREEN));
		}

		MutableComponent flavorTextComponent = new TranslatableComponent(flavorTextKey);
		if (!flavorTextComponent.getString().equals(flavorTextKey)) {
			tooltip.add(flavorTextComponent.withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		}
	}

	public static void appendGeneratorDetails(ItemStack stack, List<Component> tooltip) {
		ItemStackItemHandler handler = new ItemStackItemHandler(stack, FUEL_INVENTORY, 1);
		ItemStack fuelStack = handler.getStackInSlot(0);
		if (Screen.hasShiftDown()) {
			tooltip.add(
					new TranslatableComponent("justdirethings.pocketgeneratorburntime",
									NBTHelpers.getIntValue(stack, COUNTER), NBTHelpers.getIntValue(stack, MAXBURN))
							.withStyle(ChatFormatting.DARK_RED));
			if (fuelStack.isEmpty())
				tooltip.add(
						new TranslatableComponent("justdirethings.pocketgeneratornofuel").withStyle(ChatFormatting.RED));
			else
				tooltip.add(new TranslatableComponent("justdirethings.pocketgeneratorfuelstack", fuelStack.getCount(),
						fuelStack.getItem().getName(fuelStack)).withStyle(ChatFormatting.DARK_AQUA));
		} else {
			if (fuelStack.isEmpty())
				tooltip.add(
						new TranslatableComponent("justdirethings.pocketgeneratornofuel").withStyle(ChatFormatting.RED));
		}
	}
}
