package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;

public class JustDireItemTags extends ItemTagsProvider {

	public JustDireItemTags(DataGenerator generator, BlockTagsProvider blockTags,
			ExistingFileHelper existingFileHelper) {
		super(generator, blockTags, JustDireThings.MODID, existingFileHelper);
	}

	public static final TagKey<Item> WRENCHES = forgeTag("wrenches");
	public static final TagKey<Item> TOOLS_WRENCH = forgeTag("tools/wrench");
	public static final TagKey<Item> RAW_FERRICORE = forgeTag("raw_materials/ferricore");
	public static final TagKey<Item> RAW_BLAZEGOLD = forgeTag("raw_materials/blazegold");
	public static final TagKey<Item> RAW_ECLIPSEALLOY = forgeTag("raw_materials/eclipsealloy");
	public static final TagKey<Item> INGOT_FERRICORE = forgeTag("ingots/ferricore");
	public static final TagKey<Item> INGOT_BLAZEGOLD = forgeTag("ingots/blazegold");
	public static final TagKey<Item> INGOT_ECLIPSEALLOY = forgeTag("ingots/eclipsealloy");
	public static final TagKey<Item> BUCKETS = forgeTag("buckets");
	public static final TagKey<Item> BUCKETS_REFINED_T2_FUEL = forgeTag("buckets/refined_t2_fuel");
	public static final TagKey<Item> BUCKETS_REFINED_T3_FUEL = forgeTag("buckets/refined_t3_fuel");
	public static final TagKey<Item> BUCKETS_REFINED_T4_FUEL = forgeTag("buckets/refined_t4_fuel");
	public static final TagKey<Item> BUCKETS_UNREFINED_T2_FUEL = forgeTag("buckets/unrefined_t2_fuel");
	public static final TagKey<Item> BUCKETS_UNREFINED_T3_FUEL = forgeTag("buckets/unrefined_t3_fuel");
	public static final TagKey<Item> BUCKETS_UNREFINED_T4_FUEL = forgeTag("buckets/unrefined_t4_fuel");
	public static final TagKey<Item> BUCKETS_PORTAL_FLUID = forgeTag("buckets/portal_fluid");
	public static final TagKey<Item> BUCKETS_UNSTABLE_PORTAL_FLUID = forgeTag("buckets/unstable_portal_fluid");
	public static final TagKey<Item> BUCKETS_TIME_FLUID = forgeTag("buckets/time_fluid");
	public static final TagKey<Item> BUCKETS_XP_FLUID = forgeTag("buckets/xp_fluid");
	public static final TagKey<Item> BUCKETS_POLYMORPHIC_FLUID = forgeTag("buckets/polymorphic_fluid");
	public static final TagKey<Item> ARMORS_BOOTS = forgeTag("armors/boots");
	public static final TagKey<Item> ARMORS_LEGGINGS = forgeTag("armors/leggings");
	public static final TagKey<Item> ARMORS_CHESTPLATES = forgeTag("armors/chestplates");
	public static final TagKey<Item> ARMORS_HELMETS = forgeTag("armors/helmets");
	public static final TagKey<Item> BOWS = forgeTag("tools/bow");
	public static final TagKey<Item> RANGED_WEAPON = forgeTag("tools/ranged_weapon");
	public static final TagKey<Item> MELEE_WEAPON = forgeTag("tools/melee_weapon");
	public static final TagKey<Item> MINING_TOOL = forgeTag("tools/mining_tool");
	public static final TagKey<Item> PAXEL = forgeTag("tools/paxel");
	public static final TagKey<Item> STORAGE_BLOCKS_CHARCOAL = forgeTag("storage_blocks/charcoal");

	private static TagKey<Item> forgeTag(String name) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
	}

	public static final TagKey<Item> FUEL_CANISTER_DENY = ModTag("deny_fuel_canister");
	public static final TagKey<Item> PARADOX_DENY = ModTag("paradox_deny");
	public static final TagKey<Item> AUTO_SMELT_DENY = ModTag("auto_smelt_deny");
	public static final TagKey<Item> AUTO_SMOKE_DENY = ModTag("auto_smoke_deny");
	public static final TagKey<Item> GOO_REVIVE_TIER_1 = ModTag("goo_revive_tier_1");
	public static final TagKey<Item> GOO_REVIVE_TIER_2 = ModTag("goo_revive_tier_2");
	public static final TagKey<Item> GOO_REVIVE_TIER_3 = ModTag("goo_revive_tier_3");
	public static final TagKey<Item> GOO_REVIVE_TIER_4 = ModTag("goo_revive_tier_4");
	// Sculk recipe ingredients, filled from Wild Backport
	public static final TagKey<Item> SCULK = ModTag("sculk");
	public static final TagKey<Item> SCULK_SHRIEKER = ModTag("sculk_shrieker");
	public static final TagKey<Item> ECHO_SHARD = ModTag("echo_shard");
	public static final TagKey<Item> GOO_RECIPE_TIER_1 = ModTag("goorecipe_tier/1");
	public static final TagKey<Item> GOO_RECIPE_TIER_2 = ModTag("goorecipe_tier/2");
	public static final TagKey<Item> GOO_RECIPE_TIER_3 = ModTag("goorecipe_tier/3");
	public static final TagKey<Item> GOO_RECIPE_TIER_4 = ModTag("goorecipe_tier/4");
	public static final List<TagKey<Item>> GOO_RECIPE_TIERS = List.of(GOO_RECIPE_TIER_1, GOO_RECIPE_TIER_2,
			GOO_RECIPE_TIER_3, GOO_RECIPE_TIER_4);

	private static TagKey<Item> ModTag(String name) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, name));
	}

	@Override
	protected void addTags() {
		tag(AUTO_SMELT_DENY);
		tag(PARADOX_DENY).add(Items.BEDROCK);
		tag(AUTO_SMOKE_DENY);
		tag(FUEL_CANISTER_DENY).add(Items.LAVA_BUCKET);
		tag(GOO_REVIVE_TIER_1).add(Items.SUGAR).add(Items.ROTTEN_FLESH);
		tag(GOO_REVIVE_TIER_2).add(Items.NETHER_WART).add(Items.BLAZE_POWDER);
		tag(GOO_REVIVE_TIER_3).add(Items.CHORUS_FRUIT).add(Items.ENDER_PEARL);
		// From Wild Backport. Tags can't read config, so the goo block checks use_sculk_recipes.
		tag(GOO_REVIVE_TIER_4).addOptional(ResourceLocation.fromNamespaceAndPath("wildbackport", "sculk"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("wildbackport", "sculk_catalyst"));
		tag(SCULK).addOptional(ResourceLocation.fromNamespaceAndPath("wildbackport", "sculk"));
		tag(SCULK_SHRIEKER).addOptional(ResourceLocation.fromNamespaceAndPath("wildbackport", "sculk_shrieker"));
		tag(ECHO_SHARD).addOptional(ResourceLocation.fromNamespaceAndPath("wildbackport", "echo_shard"));
		tag(Tags.Items.INGOTS).add(Registration.FerricoreIngot.get()).add(Registration.BlazegoldIngot.get())
				.add(Registration.EclipseAlloyIngot.get());
		tag(Tags.Items.RAW_MATERIALS).add(Registration.RawFerricore.get()).add(Registration.RawBlazegold.get())
				.add(Registration.RawEclipseAlloy.get());
		tag(Tags.Items.GEMS).add(Registration.Celestigem.get());
		tag(RAW_FERRICORE).add(Registration.RawFerricore.get());
		tag(RAW_BLAZEGOLD).add(Registration.RawBlazegold.get());
		tag(RAW_ECLIPSEALLOY).add(Registration.RawEclipseAlloy.get());
		tag(INGOT_FERRICORE).add(Registration.FerricoreIngot.get());
		tag(INGOT_BLAZEGOLD).add(Registration.BlazegoldIngot.get());
		tag(INGOT_ECLIPSEALLOY).add(Registration.EclipseAlloyIngot.get());
		tag(WRENCHES).add(Registration.FerricoreWrench.get());
		tag(TOOLS_WRENCH).add(Registration.FerricoreWrench.get());
		tag(BUCKETS_REFINED_T2_FUEL).add(Registration.REFINED_T2_FLUID_BUCKET.get());
		tag(BUCKETS_REFINED_T3_FUEL).add(Registration.REFINED_T3_FLUID_BUCKET.get());
		tag(BUCKETS_REFINED_T4_FUEL).add(Registration.REFINED_T4_FLUID_BUCKET.get());
		tag(BUCKETS_UNREFINED_T2_FUEL).add(Registration.UNREFINED_T2_FLUID_BUCKET.get());
		tag(BUCKETS_UNREFINED_T3_FUEL).add(Registration.UNREFINED_T3_FLUID_BUCKET.get());
		tag(BUCKETS_UNREFINED_T4_FUEL).add(Registration.UNREFINED_T4_FLUID_BUCKET.get());
		tag(BUCKETS_PORTAL_FLUID).add(Registration.PORTAL_FLUID_BUCKET.get());
		tag(BUCKETS_UNSTABLE_PORTAL_FLUID).add(Registration.UNSTABLE_PORTAL_FLUID_BUCKET.get());
		tag(BUCKETS_TIME_FLUID).add(Registration.TIME_FLUID_BUCKET.get());
		tag(BUCKETS_XP_FLUID).add(Registration.XP_FLUID_BUCKET.get());
		tag(BUCKETS_POLYMORPHIC_FLUID).add(Registration.POLYMORPHIC_FLUID_BUCKET.get());
		tag(BUCKETS).addTag(BUCKETS_REFINED_T2_FUEL).addTag(BUCKETS_REFINED_T3_FUEL).addTag(BUCKETS_REFINED_T4_FUEL)
				.addTag(BUCKETS_UNREFINED_T2_FUEL).addTag(BUCKETS_UNREFINED_T3_FUEL).addTag(BUCKETS_UNREFINED_T4_FUEL)
				.addTag(BUCKETS_PORTAL_FLUID).addTag(BUCKETS_UNSTABLE_PORTAL_FLUID).addTag(BUCKETS_TIME_FLUID)
				.addTag(BUCKETS_XP_FLUID).addTag(BUCKETS_POLYMORPHIC_FLUID);
		tag(ARMORS_BOOTS).add(Registration.FerricoreBoots.get()).add(Registration.BlazegoldBoots.get())
				.add(Registration.CelestigemBoots.get()).add(Registration.EclipseAlloyBoots.get());
		tag(ARMORS_LEGGINGS).add(Registration.FerricoreLeggings.get()).add(Registration.BlazegoldLeggings.get())
				.add(Registration.CelestigemLeggings.get()).add(Registration.EclipseAlloyLeggings.get());
		tag(ARMORS_CHESTPLATES).add(Registration.FerricoreChestplate.get())
				.add(Registration.BlazegoldChestplate.get()).add(Registration.CelestigemChestplate.get())
				.add(Registration.EclipseAlloyChestplate.get());
		tag(ARMORS_HELMETS).add(Registration.FerricoreHelmet.get()).add(Registration.BlazegoldHelmet.get())
				.add(Registration.CelestigemHelmet.get()).add(Registration.EclipseAlloyHelmet.get());
		tag(Tags.Items.STORAGE_BLOCKS).add(Registration.FerricoreBlock_ITEM.get())
				.add(Registration.BlazeGoldBlock_ITEM.get()).add(Registration.CelestigemBlock_ITEM.get())
				.add(Registration.EclipseAlloyBlock_ITEM.get()).add(Registration.CharcoalBlock_ITEM.get());
		tag(STORAGE_BLOCKS_CHARCOAL).add(Registration.CharcoalBlock_ITEM.get());
		tag(BOWS).add(Registration.FerricoreBow.get()).add(Registration.BlazegoldBow.get())
				.add(Registration.CelestigemBow.get()).add(Registration.EclipseAlloyBow.get());
		tag(RANGED_WEAPON).add(Registration.FerricoreBow.get()).add(Registration.BlazegoldBow.get())
				.add(Registration.CelestigemBow.get()).add(Registration.EclipseAlloyBow.get());
		tag(MELEE_WEAPON).add(Registration.FerricoreSword.get()).add(Registration.FerricoreAxe.get())
				.add(Registration.BlazegoldSword.get()).add(Registration.BlazegoldAxe.get())
				.add(Registration.CelestigemSword.get()).add(Registration.CelestigemAxe.get())
				.add(Registration.EclipseAlloySword.get()).add(Registration.EclipseAlloyAxe.get())
				.add(Registration.CelestigemPaxel.get()).add(Registration.EclipseAlloyPaxel.get());
		tag(MINING_TOOL).add(Registration.CelestigemPaxel.get()).add(Registration.EclipseAlloyPaxel.get());
		tag(PAXEL).add(Registration.CelestigemPaxel.get()).add(Registration.EclipseAlloyPaxel.get());
		tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(Registration.FerricorePickaxe.get())
				.add(Registration.BlazegoldPickaxe.get()).add(Registration.CelestigemPickaxe.get())
				.add(Registration.EclipseAlloyPickaxe.get()).add(Registration.CelestigemPaxel.get())
				.add(Registration.EclipseAlloyPaxel.get());
		tag(GOO_RECIPE_TIER_1).add(Registration.GooBlock_Tier1_ITEM.get()).addOptionalTag(GOO_RECIPE_TIER_2.location());
		tag(GOO_RECIPE_TIER_2).add(Registration.GooBlock_Tier2_ITEM.get()).addOptionalTag(GOO_RECIPE_TIER_3.location());
		tag(GOO_RECIPE_TIER_3).add(Registration.GooBlock_Tier3_ITEM.get()).addOptionalTag(GOO_RECIPE_TIER_4.location());
		tag(GOO_RECIPE_TIER_4).add(Registration.GooBlock_Tier4_ITEM.get());
	}
}
