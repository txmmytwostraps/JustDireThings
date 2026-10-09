package com.direwolf20.justdirethings.common.items.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface LeftClickableTool {
	static void setBindingMode(ItemStack stack, Ability ability, int mode) {
		stack.getOrCreateTag().putInt("bindingMode_" + ability.getName(), mode);
	}

	static int getBindingMode(ItemStack stack, Ability ability) {
		CompoundTag tag = stack.getTag();
		return tag == null ? 0 : tag.getInt("bindingMode_" + ability.getName());
	}

	static void removeFromLeftClickList(ItemStack stack, Ability ability) {
		Set<Ability> abilityList = getLeftClickList(stack);
		abilityList.remove(ability);
		setLeftClickList(stack, abilityList);
	}

	static void addToLeftClickList(ItemStack stack, Ability ability) {
		Set<Ability> abilityList = getLeftClickList(stack);
		abilityList.add(ability);
		setLeftClickList(stack, abilityList);
	}

	static void setLeftClickList(ItemStack stack, Set<Ability> abilityList) {
		ListTag abilitiesNamesList = new ListTag();
		for (Ability ability : abilityList) {
			abilitiesNamesList.add(StringTag.valueOf(ability.getName()));
		}
		stack.getOrCreateTag().put("leftClickAbilities", abilitiesNamesList);
	}

	static Set<Ability> getLeftClickList(ItemStack stack) {
		Set<Ability> abilities = new HashSet<>();
		CompoundTag tag = stack.getTag();
		if (tag == null) return abilities;
		for (Tag abilityName : tag.getList("leftClickAbilities", Tag.TAG_STRING)) {
			if (getBindingMode(stack, Ability.byName(abilityName.getAsString())) == 1)
				abilities.add(Ability.byName(abilityName.getAsString()));
		}
		return abilities;
	}

	static ToolRecords.AbilityBinding getAbilityBinding(ItemStack stack, Ability ability) {
		List<ToolRecords.AbilityBinding> abilityBindings = getCustomBindingList(stack);
		return abilityBindings.stream().filter(k -> k.abilityName().equals(ability.getName()))
				.findFirst()
				.orElse(null);
	}

	static void removeFromCustomBindingList(ItemStack stack, Ability ability) {
		List<ToolRecords.AbilityBinding> abilityBindings = new ArrayList<>(getCustomBindingList(stack));
		abilityBindings.removeIf(k -> k.abilityName().equals(ability.getName()));
		setCustomBindingList(stack, abilityBindings);
	}

	static void addToCustomBindingList(ItemStack stack, ToolRecords.AbilityBinding binding) {
		Ability ability = Ability.byName(binding.abilityName());
		removeFromCustomBindingList(stack, ability);
		List<ToolRecords.AbilityBinding> abilityList = getCustomBindingList(stack);
		abilityList.add(binding);
		setCustomBindingList(stack, abilityList);
	}

	static void setCustomBindingList(ItemStack stack, List<ToolRecords.AbilityBinding> abilityList) {
		ListTag listTag = new ListTag();
		for (ToolRecords.AbilityBinding binding : abilityList) {
			listTag.add(binding.toTag());
		}
		stack.getOrCreateTag().put("customBindingAbilities", listTag);
	}

	static List<ToolRecords.AbilityBinding> getCustomBindingList(ItemStack stack) {
		List<ToolRecords.AbilityBinding> abilityBindings = new ArrayList<>();
		CompoundTag tag = stack.getTag();
		if (tag == null) return abilityBindings;
		ListTag listTag = tag.getList("customBindingAbilities", Tag.TAG_COMPOUND);
		for (int i = 0; i < listTag.size(); i++) {
			abilityBindings.add(ToolRecords.AbilityBinding.fromTag(listTag.getCompound(i)));
		}
		return abilityBindings;
	}

	static List<Ability> getCustomBindingListFor(ItemStack stack, int key, boolean isMouse, Player player) {
		List<Ability> returnSet = new ArrayList<>();
		List<ToolRecords.AbilityBinding> abilityBindings = getCustomBindingList(stack);
		boolean isEquipped = ToggleableTool.isItemEquipped(stack, player);
		returnSet.addAll(
				abilityBindings.stream().filter(k -> k.isMouse() == isMouse &&
								k.key() == key &&
								getBindingMode(stack, Ability.byName(k.abilityName())) == 2 &&
								(!k.requireEquipped() || isEquipped))
						.map(ToolRecords.AbilityBinding::abilityName)
						.map(Ability::byName)
						.toList()
		);
		return returnSet;
	}
}
