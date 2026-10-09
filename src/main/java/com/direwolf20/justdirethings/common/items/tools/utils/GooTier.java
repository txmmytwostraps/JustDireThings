package com.direwolf20.justdirethings.common.items.tools.utils;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;
import java.util.function.Supplier;

public enum GooTier implements Tier {
	FERRICORE(2, 500, 7.0F, 2.5F, 15, () -> Ingredient.of(Registration.FerricoreIngot.get())),
	BLAZEGOLD(3, 1440, 12.0F, 3.0F, 22, () -> Ingredient.of(Registration.BlazegoldIngot.get())),
	CELESTIGEM(3, 1561, 10.0F, 4.0F, 18, () -> Ingredient.of(Registration.Celestigem.get())),
	ECLIPSEALLOY(4, 2561, 16.0F, 5.0F, 25, () -> Ingredient.of(Registration.EclipseAlloyIngot.get()));

	// Each one mines like the vanilla tier it sorts after
	static {
		TierSortingRegistry.registerTier(FERRICORE, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "ferricore"), List.of(Tiers.IRON), List.of(Tiers.DIAMOND));
		TierSortingRegistry.registerTier(BLAZEGOLD, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "blazegold"), List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE));
		TierSortingRegistry.registerTier(CELESTIGEM, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "celestigem"), List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE));
		TierSortingRegistry.registerTier(ECLIPSEALLOY, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "eclipsealloy"), List.of(Tiers.NETHERITE), List.of());
	}

	private final int level;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final LazyLoadedValue<Ingredient> repairIngredient;

	GooTier(int pLevel, int pUses, float pSpeed, float pDamage, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
		this.level = pLevel;
		this.uses = pUses;
		this.speed = pSpeed;
		this.damage = pDamage;
		this.enchantmentValue = pEnchantmentValue;
		this.repairIngredient = new LazyLoadedValue<>(pRepairIngredient);
	}

	@Override
	public int getUses() {
		return this.uses;
	}

	@Override
	public float getSpeed() {
		return this.speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.damage;
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}
}
