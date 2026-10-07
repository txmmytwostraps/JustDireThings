package com.direwolf20.justdirethings;

import com.direwolf20.justdirethings.common.entities.DecoyEntity;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.datagen.recipes.SculkRecipesCondition;
import com.direwolf20.justdirethings.setup.ClientSetup;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(JustDireThings.MODID)
public class JustDireThings {
	public static final String MODID = "justdirethings";

	public JustDireThings() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		Registration.init(modEventBus);
		Config.register();

		modEventBus.addListener(ModSetup::init);
		modEventBus.addListener(PacketHandler::registerNetworking);
		modEventBus.addListener(Config::onConfigReloading);
		modEventBus.addListener(JustDireThings::registerEntityAttributes);
		modEventBus.addListener(JustDireThings::registerEntityTypeAttributes);
		modEventBus.addGenericListener(RecipeSerializer.class, JustDireThings::registerRecipeConditions);
		if (FMLLoader.getDist().isClient()) {
			modEventBus.addListener(ClientSetup::init);
		}
	}

	private static void registerEntityAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, Registration.PHASE.get());
	}

	private static void registerEntityTypeAttributes(EntityAttributeCreationEvent event) {
		event.put(Registration.DecoyEntity.get(), DecoyEntity.createAttributes().build());
	}

	private static void registerRecipeConditions(RegistryEvent.Register<RecipeSerializer<?>> event) {
		CraftingHelper.register(SculkRecipesCondition.Serializer.INSTANCE);
	}
}
