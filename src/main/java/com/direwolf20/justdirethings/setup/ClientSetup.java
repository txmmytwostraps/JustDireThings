package com.direwolf20.justdirethings.setup;


import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.KeyBindings;
import com.direwolf20.justdirethings.client.blockentityrenders.*;
import com.direwolf20.justdirethings.client.blockentityrenders.gooblocks.GooBlockRender_Tier1;
import com.direwolf20.justdirethings.client.blockentityrenders.gooblocks.GooBlockRender_Tier2;
import com.direwolf20.justdirethings.client.blockentityrenders.gooblocks.GooBlockRender_Tier3;
import com.direwolf20.justdirethings.client.blockentityrenders.gooblocks.GooBlockRender_Tier4;
import com.direwolf20.justdirethings.client.entitymodels.PortalProjectileModel;
import com.direwolf20.justdirethings.client.entityrenders.*;
import com.direwolf20.justdirethings.client.events.RenderHighlight;
import com.direwolf20.justdirethings.client.events.RenderLevelLast;
import com.direwolf20.justdirethings.client.overlays.AbilityCooldownOverlay;
import com.direwolf20.justdirethings.client.renderers.FluidContainerBakedModel;
import com.direwolf20.justdirethings.client.renderers.shader.DireRenderTypes;
import com.direwolf20.justdirethings.client.screens.*;
import com.direwolf20.justdirethings.common.items.FluidCanister;
import com.direwolf20.justdirethings.common.items.PocketGenerator;
import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.items.PotionCanister;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseBow;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.ItemMultiLayerBakedModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = JustDireThings.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
	public static void init(final FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(KeyBindings::onClientInput);

		//Register our Render Events Class
		MinecraftForge.EVENT_BUS.register(RenderLevelLast.class);
		MinecraftForge.EVENT_BUS.register(RenderHighlight.class);

		//Item Properties
		event.enqueueWork(() -> {
			for (var tool : Registration.TOOLS.getEntries()) {
				registerEnabledToolTextures(tool.get());
			}
			registerEnabledToolTextures(Registration.Pocket_Generator.get());
			for (var bow : Registration.BOWS.getEntries()) {
				if (bow.get() instanceof BaseBow baseBow) {
					ItemProperties.register(bow.get(), ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "pull"), (stack, level, living, id) -> {
						if (living == null || living.getUseItem() != stack) return 0.0F;
						return (stack.getUseDuration() - (living.getUseItemRemainingTicks() + (20 - baseBow.getMaxDraw()))) / baseBow.getMaxDraw();
					});
					ItemProperties.register(bow.get(), ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "pulling"), (stack, level, living, id) -> {
						return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
					});
				}
			}
		});

		event.enqueueWork(() -> {
			ItemProperties.register(Registration.FluidCanister.get(),
					ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "fullness"), (stack, level, living, id) -> FluidCanister.getFullness(stack));
		});

		event.enqueueWork(() -> {
			ItemProperties.register(Registration.PotionCanister.get(),
					ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "potion_fullness"), (stack, level, living, id) -> PotionCanister.getFullness(stack));
		});

		event.enqueueWork(() -> {
			ItemProperties.register(Registration.PortalGunV2.get(),
					ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "fullness"), (stack, level, living, id) -> PortalGunV2.getFullness(stack));
		});

		ItemBlockRenderTypes.setRenderLayer(Registration.UNSTABLE_PORTAL_FLUID_SOURCE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(Registration.UNSTABLE_PORTAL_FLUID_FLOWING.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(Registration.TIME_FLUID_SOURCE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(Registration.TIME_FLUID_FLOWING.get(), RenderType.translucent());

		//Models can't set their own render type
		ItemBlockRenderTypes.setRenderLayer(Registration.EclipseGateBlock.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(Registration.GooPatternBlock.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(Registration.TimeCrystalCluster_Small.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(Registration.TimeCrystalCluster_Medium.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(Registration.TimeCrystalCluster_Large.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(Registration.TimeCrystalCluster.get(), RenderType.cutout());
	}

	@SubscribeEvent
	public static void registerOverlays(FMLClientSetupEvent event) {
		OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "abilitycooldownoverlay", AbilityCooldownOverlay.INSTANCE);
	}

	public static void registerEnabledToolTextures(Item tool) {
		if (tool instanceof ToggleableItem toggleableItem) {
			ItemProperties.register(tool,
					ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "enabled"), (stack, level, living, id) -> {
						if (stack.getItem() instanceof PocketGenerator) {
							if (!toggleableItem.getEnabled(stack)) return 0.0f;
							IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
							if (energyStorage == null) return 0.0f;
							if (energyStorage.getEnergyStored() > 0) return 1.0f;
							if (!(NBTHelpers.getIntValue(stack, PocketGenerator.COUNTER) > 0)) return 0.0f;
							return 1.0f;
						} else
							return toggleableItem.getEnabled(stack) ? 1.0f : 0.0f;
					});
		}
	}

	@SubscribeEvent
	public static void mrl(ModelRegistryEvent e) {
		ForgeModelBakery.addSpecialModel(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "item/creaturecatcher_base"));
	}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		wrapFluidContainerModel(event, Registration.FluidCanister.getId());
		for (var bucket : Registration.BUCKET_ITEMS.getEntries()) {
			wrapFluidContainerModel(event, bucket.getId());
		}
	}

	private static void wrapFluidContainerModel(ModelBakeEvent event, ResourceLocation item) {
		ModelResourceLocation location = new ModelResourceLocation(item, "inventory");
		BakedModel model = event.getModelRegistry().get(location);
		if (model instanceof ItemMultiLayerBakedModel && event.getModelLoader().getModel(location) instanceof BlockModel blockModel)
			event.getModelRegistry().put(location, new FluidContainerBakedModel(model, blockModel.getTransforms()));
	}

	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(PortalProjectileModel.Portal_Projectile_Layer, PortalProjectileModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) {
		try {
			for(DireRenderTypes.ShaderRenderType type : DireRenderTypes.getRenderTypes().values()) {
				type.register(event.getResourceManager(), event::registerShader);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SubscribeEvent
	public static void registerScreens(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(Registration.FuelCanister_Container.get(), FuelCanisterScreen::new);
			MenuScreens.register(Registration.PocketGenerator_Container.get(), PocketGeneratorScreen::new);
			MenuScreens.register(Registration.Tool_Settings_Container.get(), ToolSettingScreen::new);
			MenuScreens.register(Registration.Item_Collector_Container.get(), ItemCollectorScreen::new);
			MenuScreens.register(Registration.BlockBreakerT1_Container.get(), BlockBreakerT1Screen::new);
			MenuScreens.register(Registration.BlockBreakerT2_Container.get(), BlockBreakerT2Screen::new);
			MenuScreens.register(Registration.BlockPlacerT1_Container.get(), BlockPlacerT1Screen::new);
			MenuScreens.register(Registration.BlockPlacerT2_Container.get(), BlockPlacerT2Screen::new);
			MenuScreens.register(Registration.ClickerT1_Container.get(), ClickerT1Screen::new);
			MenuScreens.register(Registration.ClickerT2_Container.get(), ClickerT2Screen::new);
			MenuScreens.register(Registration.SensorT1_Container.get(), SensorT1Screen::new);
			MenuScreens.register(Registration.SensorT2_Container.get(), SensorT2Screen::new);
			MenuScreens.register(Registration.DropperT1_Container.get(), DropperT1Screen::new);
			MenuScreens.register(Registration.DropperT2_Container.get(), DropperT2Screen::new);
			MenuScreens.register(Registration.GeneratorT1_Container.get(), GeneratorT1Screen::new);
			MenuScreens.register(Registration.GeneratorFluidT1_Container.get(), GeneratorFluidT1Screen::new);
			MenuScreens.register(Registration.EnergyTransmitter_Container.get(), EnergyTransmitterScreen::new);
			MenuScreens.register(Registration.BlockSwapperT1_Container.get(), BlockSwapperT1Screen::new);
			MenuScreens.register(Registration.BlockSwapperT2_Container.get(), BlockSwapperT2Screen::new);
			MenuScreens.register(Registration.PlayerAccessor_Container.get(), PlayerAccessorScreen::new);
			MenuScreens.register(Registration.FluidPlacerT1_Container.get(), FluidPlacerT1Screen::new);
			MenuScreens.register(Registration.FluidPlacerT2_Container.get(), FluidPlacerT2Screen::new);
			MenuScreens.register(Registration.FluidCollectorT1_Container.get(), FluidCollectorT1Screen::new);
			MenuScreens.register(Registration.FluidCollectorT2_Container.get(), FluidCollectorT2Screen::new);
			MenuScreens.register(Registration.PotionCanister_Container.get(), PotionCanisterScreen::new);
			MenuScreens.register(Registration.ParadoxMachine_Container.get(), ParadoxMachineScreen::new);
			MenuScreens.register(Registration.InventoryHolder_Container.get(), InventoryHolderScreen::new);
			MenuScreens.register(Registration.Experience_Holder_Container.get(), ExperienceHolderScreen::new);
			MenuScreens.register(Registration.DireSmithingTable_Container.get(), DireSmithingTableScreen::new);
		});
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		//Register Block Entity Renders
		event.registerBlockEntityRenderer(Registration.GooBlockBE_Tier1.get(), GooBlockRender_Tier1::new);
		event.registerBlockEntityRenderer(Registration.GooBlockBE_Tier2.get(), GooBlockRender_Tier2::new);
		event.registerBlockEntityRenderer(Registration.GooBlockBE_Tier3.get(), GooBlockRender_Tier3::new);
		event.registerBlockEntityRenderer(Registration.GooBlockBE_Tier4.get(), GooBlockRender_Tier4::new);
		event.registerBlockEntityRenderer(Registration.ItemCollectorBE.get(), ItemCollectorRenderer::new);
		event.registerBlockEntityRenderer(Registration.BlockBreakerT2BE.get(), BlockBreakerT2BER::new);
		event.registerBlockEntityRenderer(Registration.BlockPlacerT2BE.get(), BlockPlacerT2BER::new);
		event.registerBlockEntityRenderer(Registration.ClickerT2BE.get(), ClickerT2BER::new);
		event.registerBlockEntityRenderer(Registration.SensorT2BE.get(), SensorT2BER::new);
		event.registerBlockEntityRenderer(Registration.DropperT2BE.get(), DropperT2BER::new);
		event.registerBlockEntityRenderer(Registration.EnergyTransmitterBE.get(), EnergyTransmitterRenderer::new);
		event.registerBlockEntityRenderer(Registration.BlockSwapperT2BE.get(), BlockSwapperT2BER::new);
		event.registerBlockEntityRenderer(Registration.EclipseGateBE.get(), EclipseGateRenderer::new);
		event.registerBlockEntityRenderer(Registration.FluidPlacerT2BE.get(), FluidPlacerT2BER::new);
		event.registerBlockEntityRenderer(Registration.FluidCollectorT2BE.get(), FluidCollectorT2BER::new);
		event.registerBlockEntityRenderer(Registration.ParadoxMachineBE.get(), ParadoxMachineBER::new);
		event.registerBlockEntityRenderer(Registration.InventoryHolderBE.get(), InventoryHolderBER::new);
		event.registerBlockEntityRenderer(Registration.ExperienceHolderBE.get(), ExperienceHolderBER::new);

		//Entities
		event.registerEntityRenderer(Registration.CreatureCatcherEntity.get(), CreatureCatcherEntityRender::new);
		event.registerEntityRenderer(Registration.PortalEntity.get(), PortalEntityRender::new);
		event.registerEntityRenderer(Registration.PortalProjectile.get(), PortalProjectileRender::new);
		event.registerEntityRenderer(Registration.DecoyEntity.get(), DecoyEntityRender::new);
		event.registerEntityRenderer(Registration.JustDireArrow.get(), JustDireArrowRenderer::new);
		event.registerEntityRenderer(Registration.JustDireAreaEffectCloud.get(), NoopRenderer::new);
		event.registerEntityRenderer(Registration.TimeWandEntity.get(), TimeWandEntityRender::new);
		event.registerEntityRenderer(Registration.ParadoxEntity.get(), ParadoxEntityRender::new);
	}

	@SubscribeEvent
	public static void itemColors(ColorHandlerEvent.Item event) {
		final ItemColors colors = event.getItemColors();

		colors.register((stack, index) -> {
			if (index == 1 && stack.getItem() instanceof FluidCanister) {
				return FluidCanister.getFluidColor(stack);
			}
			return 0xFFFFFFFF;
		}, Registration.FluidCanister.get());

		colors.register((stack, index) -> {
			if (index == 1 && stack.getItem() instanceof PotionCanister) {
				return PotionCanister.getPotionColor(stack);
			}
			return 0xFFFFFFFF;
		}, Registration.PotionCanister.get());
	}
}
