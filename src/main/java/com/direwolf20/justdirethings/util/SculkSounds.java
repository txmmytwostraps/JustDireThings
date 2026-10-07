package com.direwolf20.justdirethings.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Wild Backport's sculk sounds, or the sculk sensor's without it.
 */
public final class SculkSounds {
	private SculkSounds() {
	}

	public static SoundEvent blockBreak() {
		return wildBackport("block.sculk.break", SoundEvents.SCULK_SENSOR_BREAK);
	}

	public static SoundEvent blockSpread() {
		return wildBackport("block.sculk.spread", SoundEvents.SCULK_SENSOR_PLACE);
	}

	public static SoundEvent shriekerShriek() {
		return wildBackport("block.sculk_shrieker.shriek", SoundEvents.SCULK_CLICKING);
	}

	private static SoundEvent wildBackport(String path, SoundEvent fallback) {
		SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("wildbackport", path));
		return sound != null ? sound : fallback;
	}
}
