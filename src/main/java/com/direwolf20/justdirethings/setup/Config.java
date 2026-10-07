package com.direwolf20.justdirethings.setup;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.ServerConfigSyncPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

	public static ForgeConfigSpec SERVER_CONFIG;

	public static ForgeConfigSpec.BooleanValue USE_SCULK_RECIPES;

	public static final String OVERLAY_POSITION = "overlay_position";
	public static ForgeConfigSpec.IntValue OVERLAY_X;
	public static ForgeConfigSpec.IntValue OVERLAY_Y;

	public static final String CATEGORY_GENERAL = "general";
	public static ForgeConfigSpec.IntValue MINIMUM_MACHINE_TICK_SPEED;
	public static ForgeConfigSpec.BooleanValue CLICKER_LIMIT_TICK_ACCELERATION;

	public static final String CATEGORY_GOO = "goo";
	public static ForgeConfigSpec.BooleanValue GOO_CAN_DIE;
	public static ForgeConfigSpec.DoubleValue GOO_DEATH_CHANCE;

	public static final String CATEGORY_ABILITIES = "abilities";
	public static Map<Ability, ForgeConfigSpec.BooleanValue> AVAILABLE_ABILITY_MAP = new HashMap<>();
	public static ForgeConfigSpec.IntValue TOOL_MAX_BREAK_FERRICORE;
	public static ForgeConfigSpec.IntValue TOOL_MAX_BREAK_BLAZEGOLD;
	public static ForgeConfigSpec.IntValue TOOL_MAX_BREAK_CELESTIGEM;
	public static ForgeConfigSpec.IntValue TOOL_MAX_BREAK_ECLIPSEALLOY;

	public static final String CATEGORY_GENERATOR_T1 = "generator_t1";
	public static ForgeConfigSpec.IntValue GENERATOR_T1_FE_PER_FUEL_TICK;
	public static ForgeConfigSpec.IntValue GENERATOR_T1_BURN_SPEED_MULTIPLIER;
	public static ForgeConfigSpec.IntValue GENERATOR_T1_MAX_FE;
	public static ForgeConfigSpec.IntValue GENERATOR_T1_FE_PER_TICK;

	public static final String CATEGORY_GENERATOR_FLUID_T1 = "generator_fluid_t1";
	public static ForgeConfigSpec.IntValue GENERATOR_FLUID_T1_MAX_FE;
	public static ForgeConfigSpec.IntValue GENERATOR_FLUID_T1_FE_PER_TICK;
	public static ForgeConfigSpec.IntValue FUEL_TIER2_FE_PER_MB;
	public static ForgeConfigSpec.IntValue FUEL_TIER3_FE_PER_MB;
	public static ForgeConfigSpec.IntValue FUEL_TIER4_FE_PER_MB;

	public static final String CATEGORY_FUEL_CANISTER = "fuel_canister";
	public static ForgeConfigSpec.IntValue FUEL_CANISTER_MINIMUM_TICKS_CONSUMED;
	public static ForgeConfigSpec.IntValue FUEL_CANISTER_MAXIMUM_FUEL;

	public static final String CATEGORY_PORTAL_GUNS = "portal_gun";
	public static ForgeConfigSpec.IntValue PORTAL_GUN_V1_RF_CAPACITY;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_V1_RF_COST;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_V2_RF_CAPACITY;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_V2_RF_COST;

	public static final String CATEGORY_TIME_WAND = "time_wand";
	public static ForgeConfigSpec.IntValue TIME_WAND_RF_CAPACITY;
	public static ForgeConfigSpec.IntValue TIMEWAND_RF_COST;
	public static ForgeConfigSpec.DoubleValue TIMEWAND_FLUID_COST;
	public static ForgeConfigSpec.ConfigValue<Integer> TIME_WAND_MAX_MULTIPLIER;
	public static ForgeConfigSpec.BooleanValue TIME_WAND_FAKE_PLAYER_ALLOWED;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> TIME_WAND_BLOCK_BLACKLIST;

	public static final String CATEGORY_PARADOX_MACHINE = "paradox_machine";
	public static ForgeConfigSpec.IntValue PARADOX_TOTAL_FLUID_CAPACITY;
	public static ForgeConfigSpec.IntValue PARADOX_TOTAL_RF_CAPACITY;
	public static ForgeConfigSpec.IntValue PARADOX_RF_PER_ENTITY;
	public static ForgeConfigSpec.IntValue PARADOX_RF_PER_BLOCK;
	public static ForgeConfigSpec.IntValue PARADOX_FLUID_PER_ENTITY;
	public static ForgeConfigSpec.IntValue PARADOX_FLUID_PER_BLOCK;
	public static ForgeConfigSpec.DoubleValue PARADOX_ENERGY_PER_BLOCK;
	public static ForgeConfigSpec.DoubleValue PARADOX_ENERGY_PER_ENTITY;
	public static ForgeConfigSpec.DoubleValue PARADOX_ENERGY_MAX;
	public static ForgeConfigSpec.BooleanValue PARADOX_RESTRICTED_MOBS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> PARADOX_BLOCK_BLACKLIST;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> PARADOX_ENTITY_BLACKLIST;

	public static final String ENERGY_TRANSMITTER_T1 = "energy_transmitter_t1";
	public static ForgeConfigSpec.DoubleValue ENERGY_TRANSMITTER_T1_LOSS_PER_BLOCK;
	public static ForgeConfigSpec.IntValue ENERGY_TRANSMITTER_T1_MAX_RF;
	public static ForgeConfigSpec.IntValue ENERGY_TRANSMITTER_T1_RF_PER_TICK;

	public static final String CATEGORY_POCKET_GENERATOR = "pocket_generator";
	public static ForgeConfigSpec.IntValue POCKET_GENERATOR_FE_PER_FUEL_TICK;
	public static ForgeConfigSpec.IntValue POCKET_GENERATOR_BURN_SPEED_MULTIPLIER;
	public static ForgeConfigSpec.IntValue POCKET_GENERATOR_MAX_FE;
	public static ForgeConfigSpec.IntValue POCKET_GENERATOR_FE_PER_TICK;

	public static final String CATEGORY_POLYMORPHIC_WAND = "polymorphic_wand";
	public static ForgeConfigSpec.IntValue POLYMORPHIC_WAND_MAX_FLUID;
	public static ForgeConfigSpec.IntValue RANDOM_POLYMORPH_COST;
	public static ForgeConfigSpec.IntValue POLYMORPHIC_WAND_V2_MAX_FLUID;
	public static ForgeConfigSpec.IntValue TARGET_POLYMORPH_COST;
	public static ForgeConfigSpec.IntValue POLYMORPHIC_WAND_V2_FE_CAPACITY;
	public static ForgeConfigSpec.IntValue POLYMORPHIC_WAND_V2_FE_COST;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> POLYMORPH_BLACKLIST;

	public static final String CATEGORY_BLOCK_SWAPPER = "block_swapper";
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> SWAPPER_ENTITY_BLACKLIST;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> SWAPPER_BLOCK_BLACKLIST;

	public static final String CATEGORY_PLAYER_ACCESSOR = "player_accessor";
	public static ForgeConfigSpec.BooleanValue PLAYER_ACCESSOR_DIMENSIONAL_BLACKLISTING;
	public static ForgeConfigSpec.IntValue PLAYER_ACCESSOR_VALIDATION_TIME;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> PLAYER_ACCESSOR_BLACKLISTED_DIMENSIONS;

	public static final String CATEGORY_TIME_CRYSTAL = "time_crystal";
	public static ForgeConfigSpec.BooleanValue TIME_CRYSTAL_CUSTOM_DIMENSIONS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> TIME_CRYSTAL_STAGE1_DIMENSIONS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> TIME_CRYSTAL_STAGE2_DIMENSIONS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> TIME_CRYSTAL_STAGE3_DIMENSIONS;

	public static final String CATEGORY_RESTRICTED_DIMENSIONS = "restricted_dimensions";
	public static ForgeConfigSpec.BooleanValue RESTRICTED_DIMENSIONS_ENABLED;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> RESTRICTED_DIMENSIONS_LIST;
	public static ForgeConfigSpec.BooleanValue RESTRICT_BLOCK_BREAKERS;
	public static ForgeConfigSpec.BooleanValue RESTRICT_BLOCK_PLACERS;
	public static ForgeConfigSpec.BooleanValue RESTRICT_BLOCK_SWAPPERS;
	public static ForgeConfigSpec.BooleanValue RESTRICT_CLICKERS;
	public static ForgeConfigSpec.BooleanValue RESTRICT_PARADOX_MACHINE;
	public static ForgeConfigSpec.BooleanValue RESTRICT_PORTAL_GUNS;
	public static ForgeConfigSpec.BooleanValue RESTRICT_TIME_WAND;
	public static ForgeConfigSpec.BooleanValue RESTRICT_CREATURE_CATCHER;
	public static ForgeConfigSpec.BooleanValue RESTRICT_PHASE;
	public static ForgeConfigSpec.BooleanValue RESTRICT_FLUID_PLACERS;
	public static ForgeConfigSpec.BooleanValue RESTRICT_VOID_SHIFT;
	public static ForgeConfigSpec.BooleanValue RESTRICT_ECLIPSEGATE;

	public static void register() {
		registerServerConfigs();
		registerCommonConfigs();
		registerClientConfigs();
	}

	private static void registerCommonConfigs() {
		USE_SCULK_RECIPES = COMMON_BUILDER.comment(
				"Use the sculk recipes (sculk items from Wild Backport) for Shadowpulse Goo and its revival and for the Ore X-Ray, Mob X-Ray and Mental Obliteration upgrades.",
				"When off, those recipes use obsidian, dragon's breath, popped chorus fruit, eye of ender and observers instead.")
				.define("use_sculk_recipes", false);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
	}

	private static void registerClientConfigs() {
		overlayConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
	}

	private static void registerServerConfigs() {
		gooConfig();
		generalConfig();
		abilityConfigs();
		generatorT1Config();
		generatorFluidT1Config();
		energyTransmitter();
		fuelCanisterConfig();
		pocketGeneratorConfig();
		portalGunConfig();
		timeWandConfig();
		polymorphWandConfig();
		paradoxConfig();
		swapperConfig();
		playerAccessorConfig();
		timeCrystalConfig();
		restrictedDimensionsConfig();
		SERVER_CONFIG = SERVER_BUILDER.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
	}

	/**
	 * Forge only syncs server config on login. Clients read it too, so resend it on every edit.
	 */
	public static void onConfigReloading(ModConfigEvent.Reloading event) {
		ModConfig config = event.getConfig();
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (config.getType() != ModConfig.Type.SERVER || server == null)
			return;
		// Called off the server thread
		server.execute(() -> {
			try {
				PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new ServerConfigSyncPayload(config.getFileName(), Files.readAllBytes(config.getFullPath())));
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	private static void overlayConfig() {
		CLIENT_BUILDER.comment("Cooldown Overlay Position").push(OVERLAY_POSITION);
		OVERLAY_X = CLIENT_BUILDER.comment("The X position of the cooldown overlay - this is pixels left from the center of the screen")
				.defineInRange("overlay_x_position", 91, -500, 500);
		OVERLAY_Y = CLIENT_BUILDER.comment("The Y position of the cooldown overlay - this is pixels up from the bottom of the screen")
				.defineInRange("overlay_y_position", 70, 0, 500);
		CLIENT_BUILDER.pop();
	}

	private static void gooConfig() {
		SERVER_BUILDER.comment("Goo settings").push(CATEGORY_GOO);
		GOO_CAN_DIE = SERVER_BUILDER.comment("Can goo randomly die, needing to be revived by right clicking with an item?")
				.define("goo_can_die", true);
		GOO_DEATH_CHANCE = SERVER_BUILDER.comment("The random chance that a goo block can 'die' when it finishes crafting. Set to 1.0 to guarantee it dies every time.  Default is 0.1, which is a 10% chance.")
				.defineInRange("goo_death_chance", 0.1, 0.0, 1.0);
		SERVER_BUILDER.pop();
	}

	private static void generalConfig() {
		SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
		MINIMUM_MACHINE_TICK_SPEED = SERVER_BUILDER.comment("The minimum tick speed machines can be set to. Defaults to 1, meaning every tick")
				.defineInRange("minimum_machine_tick_speed", 1, 1, 100);
		CLICKER_LIMIT_TICK_ACCELERATION = SERVER_BUILDER.comment(
				"If true, Clickers (T1/T2) will only perform their click once per real game tick, even if a tick-accelerator mod invokes them multiple times within the same tick.",
				"Each click simulates a full player interaction, so tick accelerators can multiply a Clicker's CPU cost; this caps that cost, but Clickers will no longer be sped up beyond their configured tick speed by tick accelerators.",
				"Defaults to true to protect server performance. Set to false to let tick accelerators fully speed up Clickers again, at the cost of the CPU multiplication this guards against.")
				.define("clicker_limit_tick_acceleration", true);
		SERVER_BUILDER.pop();
	}

	private static void abilityConfigs() {
		SERVER_BUILDER.comment("Ability settings: For disabling certain abilities").push(CATEGORY_ABILITIES);
		for (Ability ability : Ability.values()) {
			AVAILABLE_ABILITY_MAP.put(ability, SERVER_BUILDER.comment(ability.getName())
					.define(ability.getName(), true));
		}
		TOOL_MAX_BREAK_FERRICORE = SERVER_BUILDER.comment("The maximum number of blocks that a Ferricore tool can break as a result of abilities, such as ore miner or tree feller.")
				.defineInRange("tool_max_break_ferricore", 64, 1, 2048);
		TOOL_MAX_BREAK_BLAZEGOLD = SERVER_BUILDER.comment("The maximum number of blocks that a Blazegold tool can break as a result of abilities, such as ore miner or tree feller.")
				.defineInRange("tool_max_break_blazegold", 128, 1, 2048);
		TOOL_MAX_BREAK_CELESTIGEM = SERVER_BUILDER.comment("The maximum number of blocks that a Celestigem tool can break as a result of abilities, such as ore miner or tree feller.")
				.defineInRange("tool_max_break_celestigem", 192, 1, 2048);
		TOOL_MAX_BREAK_ECLIPSEALLOY = SERVER_BUILDER.comment("The maximum number of blocks that a Eclipse Alloy tool can break as a result of abilities, such as ore miner or tree feller.")
				.defineInRange("tool_max_break_eclipsealloy", 256, 1, 2048);
		SERVER_BUILDER.pop();
	}

	private static void generatorT1Config() {
		SERVER_BUILDER.comment("Generator T1").push(CATEGORY_GENERATOR_T1);
		GENERATOR_T1_FE_PER_FUEL_TICK = SERVER_BUILDER.comment("The amount of Forge Energy created per burn tick of fuel. Coal has 1600 burn ticks. Sticks have 100 burn ticks.")
				.defineInRange("generator_t1_fe_per_fuel_tick", 15, 1, Integer.MAX_VALUE);
		GENERATOR_T1_BURN_SPEED_MULTIPLIER = SERVER_BUILDER.comment("The multiplier for the burn speed, making the generator run faster. Coal is 1600 ticks to burn, if you set this to 10, it will burn in 160 ticks")
				.defineInRange("generator_t1_burn_speed_multiplier", 4, 1, 1000);
		GENERATOR_T1_MAX_FE = SERVER_BUILDER.comment("The maximum amount of Forge Energy the generator can hold in its buffer")
				.defineInRange("generator_t1_max_fe", 1000000, 1, Integer.MAX_VALUE);
		GENERATOR_T1_FE_PER_TICK = SERVER_BUILDER.comment("The FE per Tick that the generator outputs")
				.defineInRange("generator_t1_fe_per_tick", 1000, 1, Integer.MAX_VALUE);
		SERVER_BUILDER.pop();
	}

	private static void generatorFluidT1Config() {
		SERVER_BUILDER.comment("Fluid Generator T1").push(CATEGORY_GENERATOR_FLUID_T1);
		GENERATOR_FLUID_T1_MAX_FE = SERVER_BUILDER.comment("The maximum amount of Forge Energy the generator can hold in its buffer")
				.defineInRange("generator_fluid_t1_max_fe", 5000000, 1, Integer.MAX_VALUE);
		GENERATOR_FLUID_T1_FE_PER_TICK = SERVER_BUILDER.comment("The FE per Tick that the generator outputs")
				.defineInRange("generator_fluid_t1_fe_per_tick", 5000, 1, Integer.MAX_VALUE);
		FUEL_TIER2_FE_PER_MB = SERVER_BUILDER.comment("The FE per produced per MB of Tier 2 fuel (Blaze Ember)")
				.defineInRange("fuel_tier2_fe_per_mb", 450, 1, Integer.MAX_VALUE);
		FUEL_TIER3_FE_PER_MB = SERVER_BUILDER.comment("The FE per produced per MB of Tier 3 fuel (Voidflame)")
				.defineInRange("fuel_tier3_fe_per_mb", 1300, 1, Integer.MAX_VALUE);
		FUEL_TIER4_FE_PER_MB = SERVER_BUILDER.comment("The FE per produced per MB of Tier 4 fuel (Eclipse Ember)")
				.defineInRange("fuel_tier4_fe_per_mb", 4000, 1, Integer.MAX_VALUE);
		SERVER_BUILDER.pop();
	}

	private static void fuelCanisterConfig() {
		SERVER_BUILDER.comment("Fuel Canister").push(CATEGORY_FUEL_CANISTER);
		FUEL_CANISTER_MINIMUM_TICKS_CONSUMED = SERVER_BUILDER.comment("The amount of ticks 'consumed' per operation in the furnace. Lower is more efficient fuel use.")
				.defineInRange("fuel_canister_minimum_ticks_consumed", 200, 100, Integer.MAX_VALUE);
		FUEL_CANISTER_MAXIMUM_FUEL = SERVER_BUILDER.comment("The maximum amount of fuel (in ticks) permitted in the fuel canister.")
				.defineInRange("fuel_canister_maximum_fuel", 10000000, 100, Integer.MAX_VALUE);
		SERVER_BUILDER.pop();
	}

	private static void energyTransmitter() {
		SERVER_BUILDER.comment("Energy Transmitter T1").push(ENERGY_TRANSMITTER_T1);
		ENERGY_TRANSMITTER_T1_MAX_RF = SERVER_BUILDER.comment("The maximum energy storage")
				.defineInRange("energy_transmitter_t1_max_rf", 1000000, 1, Integer.MAX_VALUE);
		ENERGY_TRANSMITTER_T1_RF_PER_TICK = SERVER_BUILDER.comment("The maximum RF transmitted per tick to machines and other transmitters")
				.defineInRange("energy_transmitter_t1_rf_per_tick", 1000, 1, Integer.MAX_VALUE);
		ENERGY_TRANSMITTER_T1_LOSS_PER_BLOCK = SERVER_BUILDER.comment("The energy loss per block distance in percent")
				.defineInRange("energy_transmitter_t1_loss_per_block", 1.0, 0, 100);
		SERVER_BUILDER.pop();
	}

	private static void pocketGeneratorConfig() {
		SERVER_BUILDER.comment("Pocket Generator").push(CATEGORY_POCKET_GENERATOR);
		POCKET_GENERATOR_FE_PER_FUEL_TICK = SERVER_BUILDER.comment("The amount of Forge Energy created per burn tick of fuel. Coal has 1600 burn ticks. Sticks have 100 burn ticks.")
				.defineInRange("pocket_gen_fe_per_fuel_tick", 15, 1, Integer.MAX_VALUE);
		POCKET_GENERATOR_BURN_SPEED_MULTIPLIER = SERVER_BUILDER.comment("The multiplier for the burn speed, making the generator run faster. Coal is 1600 ticks to burn, if you set this to 10, it will burn in 160 ticks")
				.defineInRange("pocket_gen_burn_speed_multiplier", 4, 1, 1000);
		POCKET_GENERATOR_MAX_FE = SERVER_BUILDER.comment("The maximum amount of Forge Energy the generator can hold in its buffer")
				.defineInRange("pocket_gen_max_fe", 1000000, 1, Integer.MAX_VALUE);
		POCKET_GENERATOR_FE_PER_TICK = SERVER_BUILDER.comment("The FE per Tick that the generator charges other items at")
				.defineInRange("pocket_gen_fe_per_tick", 5000, 1, Integer.MAX_VALUE);
		SERVER_BUILDER.pop();
	}

	private static void portalGunConfig() {
		SERVER_BUILDER.comment("Portal Guns").push(CATEGORY_PORTAL_GUNS);
		PORTAL_GUN_V1_RF_CAPACITY = SERVER_BUILDER.comment("The maximum amount of Forge Energy the Portal Gun (V1) can hold in its buffer")
				.defineInRange("portal_gun_v1_rf_capacity", 100000, 1, Integer.MAX_VALUE);
		PORTAL_GUN_V1_RF_COST = SERVER_BUILDER.comment("The Forge Energy cost to fire the Portal Gun (V1) projectile")
				.defineInRange("portal_gun_v1_rf_cost", 1000, 1, Integer.MAX_VALUE);
		PORTAL_GUN_V2_RF_CAPACITY = SERVER_BUILDER.comment("The maximum amount of Forge Energy the Portal Gun (V2) can hold in its buffer")
				.defineInRange("portal_gun_v2_rf_capacity", 1000000, 1, Integer.MAX_VALUE);
		PORTAL_GUN_V2_RF_COST = SERVER_BUILDER.comment("The Forge Energy cost to fire the Portal Gun (V2) projectile")
				.defineInRange("portal_gun_v2_rf_cost", 5000, 1, Integer.MAX_VALUE);
		SERVER_BUILDER.pop();
	}

	private static void timeWandConfig() {
		SERVER_BUILDER.comment("Time Wand").push(CATEGORY_TIME_WAND);
		TIME_WAND_RF_CAPACITY = SERVER_BUILDER.comment("The maximum amount of Forge Energy the Time Wand can hold in its buffer")
				.defineInRange("time_wand_rf_capacity", 100000, 1, Integer.MAX_VALUE);
		TIMEWAND_RF_COST = SERVER_BUILDER.comment("The Forge Energy cost to use the Time wand. This value is multiplied by the acceleration amount. For example, when you go from 128 to 256, it will charge 256 x <this value> in FE cost.")
				.defineInRange("time_wand_rf_cost", 100, 0, Integer.MAX_VALUE);
		TIMEWAND_FLUID_COST = SERVER_BUILDER.comment("The Time Fluid cost to use the time wand.  This value is multiplied by the acceleration amount. For example, when you go from 128 to 256, it will charge 256 x <this value> in Time Fluid.")
				.defineInRange("time_wand_fluid_cost", 0.5, 0, Double.MAX_VALUE);
		TIME_WAND_MAX_MULTIPLIER = SERVER_BUILDER.comment("The maximum speed multiplier that can be applied using a Time Wand. This value should be a power of two.")
				.define("time_wand_max_multiplier", 256, value -> {
					if (value == null) {
						LOGGER.warn("time_wand_max_multiplier is null, creating a default entry in the config.");
						return false; // Return false to ensure the entry is created
					}
					final boolean validPowerOfTwo = (int) Math.pow(2, logBase2((int) value)) == (int) value;
					if (!validPowerOfTwo || (int) value < 2) {
						LOGGER.error("Invalid time_wand_max_multiplier {}, must be power of 2 and >=2", value);
					}
					return validPowerOfTwo;
				});
		TIME_WAND_FAKE_PLAYER_ALLOWED = SERVER_BUILDER.comment("Can fake players use the Time Wand (Like in the clickers)?")
				.define("time_wand_fake_player_allowed", true);
		TIME_WAND_BLOCK_BLACKLIST = SERVER_BUILDER.comment(
				"Blocks the Time Wand cannot accelerate, matched by regex against the full block ID.",
				"Each entry is a regex searched (not fully matched) against the block ID, so partial matches work.",
				"Example: \"draconicevolution:chaos_crystal\" blocks that exact block,",
				"or \"^draconicevolution:\" blocks all blocks from that mod.",
				"Note: the tick_speed_deny block tag also controls this and is checked first.")
				.defineListAllowEmpty(List.of("time_wand_block_blacklist"), () -> List.of(), s -> s instanceof String);
		SERVER_BUILDER.pop();
	}

	private static void polymorphWandConfig() {
		SERVER_BUILDER.comment("Polymorphic Wand").push(CATEGORY_POLYMORPHIC_WAND);
		RANDOM_POLYMORPH_COST = SERVER_BUILDER.comment("The polymorphic fluid cost to use the polymorph wand")
				.defineInRange("polymorphic_wand_fluid_cost", 100, 1, Integer.MAX_VALUE);
		POLYMORPHIC_WAND_MAX_FLUID = SERVER_BUILDER.comment("The amount of fluid the Polymorphic Wand can hold")
				.defineInRange("polymorphic_wand_max_fluid", 2000, 1, Integer.MAX_VALUE);
		POLYMORPHIC_WAND_V2_FE_COST = SERVER_BUILDER.comment("The FE cost to use the Advanced Polymorphic Wand")
				.defineInRange("polymorphic_wand_v2_fe_cost", 50000, 1, Integer.MAX_VALUE);
		POLYMORPHIC_WAND_V2_FE_CAPACITY = SERVER_BUILDER.comment("The amount of FE the Advanced Polymorphic Wand can hold")
				.defineInRange("polymorphic_wand_v2_fe_capacity", 1000000, 1, Integer.MAX_VALUE);
		TARGET_POLYMORPH_COST = SERVER_BUILDER.comment("The polymorphic fluid cost to use the Advanced Polymorphic Wand")
				.defineInRange("polymorphic_wand_v2_fluid_cost", 250, 1, Integer.MAX_VALUE);
		POLYMORPHIC_WAND_V2_MAX_FLUID = SERVER_BUILDER.comment("The amount of fluid the Advanced Polymorphic Wand can hold")
				.defineInRange("polymorphic_wand_v2_max_fluid", 8000, 1, Integer.MAX_VALUE);
		POLYMORPH_BLACKLIST = SERVER_BUILDER.comment(
				"Entities the Polymorphic Wand cannot create, matched by regex against the full entity ID.",
				"Each entry is a regex searched (not fully matched) against the entity ID, so partial matches work.",
				"Example: \"somebossmod:big_boss\" blocks that exact entity,",
				"or \"^somebossmod:\" blocks all entities from that mod.")
				.defineListAllowEmpty(List.of("polymorph_blacklist"), () -> List.of(), s -> s instanceof String);
		SERVER_BUILDER.pop();
	}

	public static int logBase2(final int value) {
		return (int) (Math.log(value) / Math.log(2));
	}

	private static void paradoxConfig() {
		SERVER_BUILDER.comment("Paradox Machine").push(CATEGORY_PARADOX_MACHINE);
		PARADOX_TOTAL_RF_CAPACITY = SERVER_BUILDER.comment("The maximum amount of Forge Energy the Paradox Machine can hold in its buffer")
				.defineInRange("paradox_rf_capacity", 10000000, 1, Integer.MAX_VALUE);
		PARADOX_RF_PER_BLOCK = SERVER_BUILDER.comment("The Forge Energy cost to Restore 1 block with the Paradox Machine.  This value is multiplied by the number of blocks the machine is restoring.")
				.defineInRange("paradox_rf_per_block", 250000, 0, Integer.MAX_VALUE);
		PARADOX_RF_PER_ENTITY = SERVER_BUILDER.comment("The Forge Energy cost to Restore 1 entity with the Paradox Machine.  This value is multiplied by the number of entities the machine is restoring.")
				.defineInRange("paradox_rf_per_entity", 250000, 0, Integer.MAX_VALUE);
		PARADOX_TOTAL_FLUID_CAPACITY = SERVER_BUILDER.comment("The maximum amount of Time Fluid the Paradox Machine can hold in its buffer")
				.defineInRange("paradox_fluid_capacity", 16000, 1, Integer.MAX_VALUE);
		PARADOX_FLUID_PER_BLOCK = SERVER_BUILDER.comment("The Time Fluid cost (in mb) to Restore 1 block with the Paradox Machine.  This value is multiplied by the number of blocks the machine is restoring.")
				.defineInRange("paradox_fluid_per_block", 50, 0, Integer.MAX_VALUE);
		PARADOX_FLUID_PER_ENTITY = SERVER_BUILDER.comment("The Time Fluid cost (in mb) to Restore 1 entity with the Paradox Machine.  This value is multiplied by the number of entities the machine is restoring.")
				.defineInRange("paradox_fluid_per_entity", 50, 0, Integer.MAX_VALUE);
		PARADOX_ENERGY_PER_BLOCK = SERVER_BUILDER.comment("The amount of Paradox Energy accumulated in the Paradox Machine when it restores a single block. This value is multiplied by the number of blocks the machine is restoring.")
				.defineInRange("paradox_energy_per_block", 0.25, 0, Double.MAX_VALUE);
		PARADOX_ENERGY_PER_ENTITY = SERVER_BUILDER.comment("The amount of Paradox Energy accumulated in the Paradox Machine when it restores a single entity. This value is multiplied by the number of blocks the machine is restoring.")
				.defineInRange("paradox_energy_per_entity", 0.25, 0, Double.MAX_VALUE);
		PARADOX_ENERGY_MAX = SERVER_BUILDER.comment("The maximum amount of Paradox Energy the Paradox Machine can hold in its buffer, before it spawns a Paradox!")
				.defineInRange("paradox_energy_max", 100, 0, Double.MAX_VALUE);
		PARADOX_RESTRICTED_MOBS = SERVER_BUILDER.comment("Use a more restrictive data filtering for mobs cloned by the paradox machine. When enabled, most modded mobs may not work very well.  When disabled, theres a small chance of item dupe bugs.  Recommended to leave this set to false, and add any mobs that allow dupes to the paradox machines deny entity tag.")
				.define("paradox_restricted_mobs", false);
		PARADOX_BLOCK_BLACKLIST = SERVER_BUILDER.comment(
				"Additional blocks the Paradox Machine cannot save/restore, matched by regex against the full block ID.",
				"Each entry is a regex searched (not fully matched) against the block ID, so partial matches work.",
				"Only blocks the Paradox Machine would otherwise be allowed to touch (the paradox_allow block tag,",
				"ores by default) are affected - this list can only narrow that, not widen it.",
				"Example: \".*reative.*\" blocks all blocks with \"reative\" in their ID (e.g. all Creative blocks),",
				"or \"seeds\" blocks any block whose ID contains \"seeds\".")
				.defineListAllowEmpty(List.of("paradox_block_blacklist"), () -> List.of(), s -> s instanceof String);
		PARADOX_ENTITY_BLACKLIST = SERVER_BUILDER.comment(
				"Additional entities the Paradox Machine cannot save/restore, matched by regex against the full entity ID.",
				"Each entry is a regex searched (not fully matched) against the entity ID, so partial matches work.",
				"Example: \"draconicevolution:chaos_guardian\" blocks that exact entity,",
				"or \"^draconicevolution:\" blocks all entities from that mod.")
				.defineListAllowEmpty(List.of("paradox_entity_blacklist"), () -> List.of(), s -> s instanceof String);
		SERVER_BUILDER.pop();
	}

	private static void swapperConfig() {
		SERVER_BUILDER.comment("Block Swapper").push(CATEGORY_BLOCK_SWAPPER);
		SWAPPER_ENTITY_BLACKLIST = SERVER_BUILDER.comment(
				"Entities the Block Swapper cannot teleport, matched by regex against the full entity ID.",
				"Each entry is a regex searched (not fully matched) against the entity ID, so partial matches work.",
				"Example: \"draconicevolution:chaos_guardian\" blocks that exact entity,",
				"or \"^draconicevolution:\" blocks all entities from that mod.")
				.defineListAllowEmpty(List.of("swapper_entity_blacklist"), () -> List.of(), s -> s instanceof String);
		SWAPPER_BLOCK_BLACKLIST = SERVER_BUILDER.comment(
				"Blocks the Block Swapper cannot move, matched by regex against the full block ID.",
				"Each entry is a regex searched (not fully matched) against the block ID, so partial matches work.",
				"Example: \"draconicevolution:chaos_crystal\" blocks that exact block,",
				"or \"^draconicevolution:\" blocks all blocks from that mod.",
				"Note: the swapper_deny block tag also controls this and is checked first.")
				.defineListAllowEmpty(List.of("swapper_block_blacklist"), () -> List.of(), s -> s instanceof String);
		SERVER_BUILDER.pop();
	}

	private static void playerAccessorConfig() {
		SERVER_BUILDER.comment("Player Accessor").push(CATEGORY_PLAYER_ACCESSOR);
		PLAYER_ACCESSOR_DIMENSIONAL_BLACKLISTING = SERVER_BUILDER.comment("Will you be blacklisting dimensions? If set to false, the Blacklist Dimensions list will not function.")
				.define("player_accessor_dimensional_blacklisting", false);
		PLAYER_ACCESSOR_VALIDATION_TIME = SERVER_BUILDER.comment("The frequency with which the player accessor validates the player - the longer this is set to, the longer the player can be accessed in a blacklisted dimension.")
				.defineInRange("player_accessor_validation_time", 100, 0, 5000);
		PLAYER_ACCESSOR_BLACKLISTED_DIMENSIONS = SERVER_BUILDER
				.comment("A list of dimension names to blacklist for the Player Accessor feature.")
				.defineListAllowEmpty(List.of("player_accessor_blacklisted_dimensions"), () -> List.of(), obj -> obj instanceof String);
		SERVER_BUILDER.pop();
	}

	private static void timeCrystalConfig() {
		SERVER_BUILDER.comment("Time Crystals").push(CATEGORY_TIME_CRYSTAL);
		TIME_CRYSTAL_CUSTOM_DIMENSIONS = SERVER_BUILDER.comment("Do you want to customize Time Crystal Growth Dimensions? If set to true, the following 3 fields MUST be populated. Don't leave any blank! Defaults to false, which means normal growth rules occur - Stage 1 = Overworld (or any other dimension besides Nether/End), Stage 2 = Nether, Stage 3 = End.")
				.define("time_crystal_custom_dimensions", false);
		TIME_CRYSTAL_STAGE1_DIMENSIONS = SERVER_BUILDER
				.comment("A list of dimensions that Time Crystals can Advance to Stage 1 in.")
				.defineListAllowEmpty(List.of("time_crystal_stage_1_dims"), () -> List.of(), obj -> obj instanceof String);
		TIME_CRYSTAL_STAGE2_DIMENSIONS = SERVER_BUILDER
				.comment("A list of dimensions that Time Crystals can Advance to Stage 2 in.")
				.defineListAllowEmpty(List.of("time_crystal_stage_2_dims"), () -> List.of(), obj -> obj instanceof String);
		TIME_CRYSTAL_STAGE3_DIMENSIONS = SERVER_BUILDER
				.comment("A list of dimensions that Time Crystals can Advance to Stage 3 in.")
				.defineListAllowEmpty(List.of("time_crystal_stage_3_dims"), () -> List.of(), obj -> obj instanceof String);
		SERVER_BUILDER.pop();
	}

	private static void restrictedDimensionsConfig() {
		SERVER_BUILDER.comment("Restricted Dimensions").push(CATEGORY_RESTRICTED_DIMENSIONS);
		RESTRICTED_DIMENSIONS_ENABLED = SERVER_BUILDER.comment("Disable the features switched on below in the dimensions listed below? If set to false, every feature works in every dimension, whatever its own switch says. A feature is disabled only while this option and its own switch are both on.")
				.define("restricted_dimensions_enabled", true);
		RESTRICTED_DIMENSIONS_LIST = SERVER_BUILDER.comment(
				"Dimensions where the features switched on below are disabled, matched by regex against the full dimension ID.",
				"Each entry is a regex searched (not fully matched) against the dimension ID, so partial matches work.",
				"Example: \"the_vault:vault\" covers every vault, since each vault runs in its own dimension, the_vault:vault_<id>.")
				.defineListAllowEmpty(List.of("restricted_dimensions_list"), () -> List.of("the_vault:vault", "the_vault:the_other_side", "the_vault:arena"), s -> s instanceof String);
		RESTRICT_BLOCK_BREAKERS = SERVER_BUILDER.comment("Disable Block Breakers (T1 and T2) in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_block_breakers", true);
		RESTRICT_BLOCK_PLACERS = SERVER_BUILDER.comment("Disable Block Placers (T1 and T2) in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_block_placers", true);
		RESTRICT_BLOCK_SWAPPERS = SERVER_BUILDER.comment("Disable Block Swappers (T1 and T2) in the restricted dimensions, and swappers bound to a position there? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_block_swappers", true);
		RESTRICT_CLICKERS = SERVER_BUILDER.comment("Disable Clickers (T1 and T2) in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_clickers", true);
		RESTRICT_PARADOX_MACHINE = SERVER_BUILDER.comment("Disable the Paradox Machine in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_paradox_machine", true);
		RESTRICT_PORTAL_GUNS = SERVER_BUILDER.comment("Disable the Portal Gun and the Advanced Portal Gun in the restricted dimensions, and Advanced Portal Gun destinations there? Closing portals and picking up Portal Fluid still work. Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_portal_guns", true);
		RESTRICT_TIME_WAND = SERVER_BUILDER.comment("Disable the Time Wand's acceleration in the restricted dimensions? Picking up Time Fluid still works. Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_time_wand", true);
		RESTRICT_CREATURE_CATCHER = SERVER_BUILDER.comment("Disable the Creature Catcher in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_creature_catcher", true);
		RESTRICT_PHASE = SERVER_BUILDER.comment("Disable Phase (walking through walls with the Eclipse Alloy Leggings, and the Eclipse Alloy Bow's Phase arrows) in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_phase", true);
		RESTRICT_FLUID_PLACERS = SERVER_BUILDER.comment("Disable Fluid Placers (T1 and T2) in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_fluid_placers", false);
		RESTRICT_VOID_SHIFT = SERVER_BUILDER.comment("Disable the Void Shift ability (Voidshift Wand and Eclipsegate Wand) in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_void_shift", false);
		RESTRICT_ECLIPSEGATE = SERVER_BUILDER.comment("Disable the Eclipse Gate ability (Eclipsegate Wand) in the restricted dimensions? Only takes effect while restricted_dimensions_enabled is also on.")
				.define("restrict_eclipsegate", false);
		SERVER_BUILDER.pop();
	}
}
