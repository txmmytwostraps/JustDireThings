package com.direwolf20.justdirethings.common.network;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.network.data.*;
import com.direwolf20.justdirethings.common.network.handler.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	private static final Optional<NetworkDirection> TO_SERVER = Optional.of(NetworkDirection.PLAY_TO_SERVER);
	private static final Optional<NetworkDirection> TO_CLIENT = Optional.of(NetworkDirection.PLAY_TO_CLIENT);

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void registerNetworking(final FMLCommonSetupEvent event) {
		int id = 0;

		//Going to Server
		CHANNEL.registerMessage(id++, AreaAffectingPayload.class, AreaAffectingPayload::write, AreaAffectingPayload::new, AreaAffectingPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, BlockStateFilterPayload.class, BlockStateFilterPayload::write, BlockStateFilterPayload::new, BlockStateFilterPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, BreakerPayload.class, BreakerPayload::write, BreakerPayload::new, BreakerPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ClickerPayload.class, ClickerPayload::write, ClickerPayload::new, ClickerPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, CopyMachineSettingsPayload.class, CopyMachineSettingsPayload::write, CopyMachineSettingsPayload::new, CopyMachineSettingsPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, DirectionSettingPayload.class, DirectionSettingPayload::write, DirectionSettingPayload::new, DirectionSettingPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, DropperSettingPayload.class, DropperSettingPayload::write, DropperSettingPayload::new, DropperSettingPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, EnergyTransmitterSettingPayload.class, EnergyTransmitterSettingPayload::write, EnergyTransmitterSettingPayload::new, EnergyTransmitterPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, FilterSettingPayload.class, FilterSettingPayload::write, FilterSettingPayload::new, FilterSettingPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, GhostSlotPayload.class, GhostSlotPayload::write, GhostSlotPayload::new, GhostSlotPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, LeftClickPayload.class, LeftClickPayload::write, LeftClickPayload::new, LeftClickPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, PlayerAccessorPayload.class, PlayerAccessorPayload::write, PlayerAccessorPayload::new, PlayerAccessorPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, PortalGunFavoritePayload.class, PortalGunFavoritePayload::write, PortalGunFavoritePayload::new, PortalGunFavoritePacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, PortalGunFavoriteChangePayload.class, PortalGunFavoriteChangePayload::write, PortalGunFavoriteChangePayload::new, PortalGunFavoriteChangePacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, PortalGunLeftClickPayload.class, PortalGunLeftClickPayload::write, PortalGunLeftClickPayload::new, PortalGunLeftClickPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, RedstoneSettingPayload.class, RedstoneSettingPayload::write, RedstoneSettingPayload::new, RedstoneSettingPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, SensorPayload.class, SensorPayload::write, SensorPayload::new, SensorPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, SwapperPayload.class, SwapperPayload::write, SwapperPayload::new, SwapperPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, TickSpeedPayload.class, TickSpeedPayload::write, TickSpeedPayload::new, TickSpeedPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ToggleToolLeftRightClickPayload.class, ToggleToolLeftRightClickPayload::write, ToggleToolLeftRightClickPayload::new, ToggleToolLeftRightClickPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ToggleToolPayload.class, ToggleToolPayload::write, ToggleToolPayload::new, ToggleToolPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ToggleToolSlotPayload.class, ToggleToolSlotPayload::write, ToggleToolSlotPayload::new, ToggleToolSlotPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ToggleToolRefreshSlots.class, ToggleToolRefreshSlots::write, ToggleToolRefreshSlots::new, ToggleToolRefreshSlotsPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ParadoxMachineSnapshotPayload.class, ParadoxMachineSnapshotPayload::write, ParadoxMachineSnapshotPayload::new, ParadoxSnapshotPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ParadoxRenderPayload.class, ParadoxRenderPayload::write, ParadoxRenderPayload::new, ParadoxRenderPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, InventoryHolderSaveSlotPayload.class, InventoryHolderSaveSlotPayload::write, InventoryHolderSaveSlotPayload::new, InventoryHolderSaveSlotPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, InventoryHolderSettingsPayload.class, InventoryHolderSettingsPayload::write, InventoryHolderSettingsPayload::new, InventoryHolderSettingsPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, InventoryHolderMoveItemsPayload.class, InventoryHolderMoveItemsPayload::write, InventoryHolderMoveItemsPayload::new, InventoryHolderMoveItemsPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ExperienceHolderPayload.class, ExperienceHolderPayload::write, ExperienceHolderPayload::new, ExperienceHolderPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ExperienceHolderSettingsPayload.class, ExperienceHolderSettingsPayload::write, ExperienceHolderSettingsPayload::new, ExperienceHolderSettingsPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ToolSettingsGUIPayload.class, ToolSettingsGUIPayload::write, ToolSettingsGUIPayload::new, ToolSettingsGUIPacket.get()::handle, TO_SERVER);
		CHANNEL.registerMessage(id++, ItemCollectorSettingsPayload.class, ItemCollectorSettingsPayload::write, ItemCollectorSettingsPayload::new, ItemCollectorSettingsPacket.get()::handle, TO_SERVER);

		//Going to Client
		CHANNEL.registerMessage(id++, ClientSoundPayload.class, ClientSoundPayload::write, ClientSoundPayload::new, ClientSoundPacket.get()::handle, TO_CLIENT);
		CHANNEL.registerMessage(id++, ParadoxSyncPayload.class, ParadoxSyncPayload::write, ParadoxSyncPayload::new, ParadoxSyncPacket.get()::handle, TO_CLIENT);
		CHANNEL.registerMessage(id++, ServerConfigSyncPayload.class, ServerConfigSyncPayload::write, ServerConfigSyncPayload::new, ServerConfigSyncPacket.get()::handle, TO_CLIENT);
	}
}
