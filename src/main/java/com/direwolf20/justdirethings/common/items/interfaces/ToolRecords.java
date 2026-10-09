package com.direwolf20.justdirethings.common.items.interfaces;

import net.minecraft.nbt.CompoundTag;

public class ToolRecords {
	public record AbilityCooldown(String abilityName, int cooldownTicks, boolean isactive) {
		public CompoundTag toTag() {
			CompoundTag tag = new CompoundTag();
			tag.putString("ability_name", abilityName);
			tag.putInt("cooldown_ticks", cooldownTicks);
			tag.putBoolean("is_active", isactive);
			return tag;
		}

		public static AbilityCooldown fromTag(CompoundTag tag) {
			return new AbilityCooldown(tag.getString("ability_name"), tag.getInt("cooldown_ticks"), tag.getBoolean("is_active"));
		}
	}

	public record AbilityBinding(String abilityName, int key, boolean isMouse, boolean requireEquipped) {
		public CompoundTag toTag() {
			CompoundTag tag = new CompoundTag();
			tag.putString("ability_name", abilityName);
			tag.putInt("key", key);
			tag.putBoolean("is_mouse", isMouse);
			tag.putBoolean("require_equipped", requireEquipped);
			return tag;
		}

		public static AbilityBinding fromTag(CompoundTag tag) {
			return new AbilityBinding(tag.getString("ability_name"), tag.getInt("key"), tag.getBoolean("is_mouse"), tag.getBoolean("require_equipped"));
		}
	}
}
