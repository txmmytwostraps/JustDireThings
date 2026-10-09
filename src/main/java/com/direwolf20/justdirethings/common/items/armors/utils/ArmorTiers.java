package com.direwolf20.justdirethings.common.items.armors.utils;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ArmorTiers implements ArmorMaterial {
	FERRICORE("ferricore", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.of(Registration.FerricoreIngot.get())),
	BLAZEGOLD("blazegold", 25, new int[]{2, 5, 6, 2}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> Ingredient.of(Registration.BlazegoldIngot.get())),
	CELESTIGEM("celestigem", 25, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> Ingredient.of(Registration.Celestigem.get())),
	ECLIPSEALLOY("eclipsealloy", 25, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> Ingredient.of(Registration.EclipseAlloyIngot.get()));

	// Indexed feet, legs, chest, head
	private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] slotProtections;
	private final int enchantmentValue;
	private final SoundEvent sound;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyLoadedValue<Ingredient> repairIngredient;

	ArmorTiers(String name, int durabilityMultiplier, int[] slotProtections, int enchantmentValue, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.slotProtections = slotProtections;
		this.enchantmentValue = enchantmentValue;
		this.sound = sound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
	}

	@Override
	public int getDurabilityForSlot(EquipmentSlot slot) {
		return HEALTH_PER_SLOT[slot.getIndex()] * this.durabilityMultiplier;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot slot) {
		return this.slotProtections[slot.getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.sound;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	// The prefix is the armor texture's namespace
	@Override
	public String getName() {
		return JustDireThings.MODID + ":" + this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
