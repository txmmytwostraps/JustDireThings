package com.direwolf20.justdirethings.common.blockentities.basebe;

import com.direwolf20.justdirethings.common.containers.handlers.FilterBasicHandler;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.UsefulFakePlayer;
import com.direwolf20.justdirethings.util.interfacehelpers.AreaAffectingData;
import com.direwolf20.justdirethings.util.interfacehelpers.FilterData;
import com.direwolf20.justdirethings.util.interfacehelpers.RedstoneControlData;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseMachineBE extends BlockEntity implements Clearable {
	public int MACHINE_SLOTS = 0;
	public int ANYSIZE_FILTER_SLOTS = 0;
	public static final UUID defaultFakePlayerUUID = UUID.fromString("4191a6f5-37fe-45d9-8ba3-4549be778e54");
	public static final GameProfile defaultFakePlayerProfile = new GameProfile(defaultFakePlayerUUID, "[JustDiresFakePlayer]");
	// One per level. Abilities break blocks in the fake player's level.
	private static final Map<ServerLevel, Map<GameProfile, FakePlayer>> fakePlayers = new HashMap<>();
	public UUID placedByUUID;
	protected int direction = 0;
	protected int tickSpeed = 20;
	protected int operationTicks = -1;
	protected UsefulFakePlayer usefulFakePlayer;
	protected final Map<ChunkPos, Boolean> chunkTestCache = new Object2BooleanOpenHashMap<>();

	// Capability holders — lazily initialized, invalidated when the BE is removed
	private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();
	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
	private LazyOptional<net.minecraftforge.fluids.capability.IFluidHandler> lazyFluidHandler = LazyOptional.empty();

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityEnergy.ENERGY && this instanceof PoweredMachineBE powered) {
			if (!lazyEnergyStorage.isPresent()) {
				lazyEnergyStorage = LazyOptional.of(powered::getEnergyStorage);
			}
			return lazyEnergyStorage.cast();
		}
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && MACHINE_SLOTS > 0) {
			if (!lazyItemHandler.isPresent()) {
				lazyItemHandler = LazyOptional.of(this::getMachineHandler);
			}
			return lazyItemHandler.cast();
		}
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this instanceof FluidMachineBE fluidMachine) {
			if (!lazyFluidHandler.isPresent()) {
				lazyFluidHandler = LazyOptional.of(fluidMachine::getFluidTank);
			}
			return lazyFluidHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		lazyEnergyStorage.invalidate();
		lazyItemHandler.invalidate();
		lazyFluidHandler.invalidate();
	}

	public BaseMachineBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	protected ItemStackHandler machineHandler;

	public ItemStackHandler getMachineHandler() {
		if (machineHandler == null) {
			machineHandler = new ItemStackHandler(MACHINE_SLOTS) {
				@Override
				protected void onContentsChanged(int slot) {
					setChanged();
				}
			};
		}
		return machineHandler;
	}

	public void tickClient() {
	}

	public void tickServer() {
		handleTicks();
		clearProtectionCache();
		if (this instanceof RedstoneControlledBE redstoneControlledBE)
			redstoneControlledBE.evaluateRedstone();
	}

	public void clearProtectionCache() {
		chunkTestCache.clear();
	}

	public void handleTicks() {
		if (operationTicks <= 0)
			operationTicks = getTickSpeed();
		operationTicks--;
	}

	/**
	 * The server minimum wins, even over a saved speed.
	 */
	public int getTickSpeed() {
		return Math.max(tickSpeed, Config.MINIMUM_MACHINE_TICK_SPEED.get());
	}

	public void setTickSpeed(int newTickSpeed) {
		this.tickSpeed = newTickSpeed;
		if (operationTicks > getTickSpeed())
			operationTicks = getTickSpeed();
		markDirtyClient();
	}

	public boolean canRun() {
		if (this instanceof RedstoneControlledBE redstoneControlledBE)
			return operationTicks == 0 || redstoneControlledBE.getRedstoneControlData().redstoneMode.equals(MiscHelpers.RedstoneMode.PULSE);
		return operationTicks == 0;
	}

	public int getDirection() {
		return direction;
	}

	public Direction getDirectionValue() {
		return Direction.values()[direction];
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	protected boolean canPlaceAt(Level level, BlockPos blockPos, FakePlayer fakePlayer) {
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (chunkTestCache.containsKey(chunkPos))
			return chunkTestCache.get(chunkPos);
		boolean canPlace = !ForgeEventFactory.onBlockPlace(fakePlayer, BlockSnapshot.create(level.dimension(), level, blockPos.below()), Direction.UP);
		chunkTestCache.put(chunkPos, canPlace);
		return canPlace;
	}

	// The place event in canPlaceAt already checks claims
	protected boolean canBreakAt(Level level, BlockPos blockPos, FakePlayer fakePlayer) {
		return true;
	}

	protected boolean canBreakAndPlaceAt(Level level, BlockPos blockPos, FakePlayer fakePlayer) {
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (chunkTestCache.containsKey(chunkPos))
			return chunkTestCache.get(chunkPos);
		boolean canBreak = canBreakAt(level, blockPos, fakePlayer) && canPlaceAt(level, blockPos, fakePlayer);
		chunkTestCache.put(chunkPos, canBreak);
		return canBreak;
	}

	protected GameProfile getPlacedByProfile() {
		if (placedByUUID == null)
			return defaultFakePlayerProfile;
		return new GameProfile(placedByUUID, "[JustDiresFakePlayer]");
	}

	protected FakePlayer getFakePlayer(ServerLevel level) {
		return fakePlayers.computeIfAbsent(level, key -> new HashMap<>()).computeIfAbsent(getPlacedByProfile(), profile -> new FakePlayer(level, profile));
	}

	public static void unloadFakePlayers(WorldEvent.Unload event) {
		if (event.getWorld() instanceof ServerLevel serverLevel)
			fakePlayers.remove(serverLevel);
	}

	protected UsefulFakePlayer getUsefulFakePlayer(ServerLevel level) {
		if (usefulFakePlayer == null)
			usefulFakePlayer = UsefulFakePlayer.createPlayer(level, getPlacedByProfile());
		return usefulFakePlayer;
	}

	public void setFakePlayerData(ItemStack itemStack, FakePlayer fakePlayer, BlockPos blockPos, Direction direction) {
		fakePlayer.setPos(blockPos.below().relative(direction).getX() + 0.5, blockPos.below().relative(direction).getY(), blockPos.below().relative(direction).getZ() + 0.5);
		float xRot = direction == Direction.DOWN ? 90 : direction == Direction.UP ? -90 : 0;
		fakePlayer.setXRot(xRot);
		fakePlayer.setYRot(direction.toYRot());
		fakePlayer.setYHeadRot(direction.toYRot());
		fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
	}

	public void setPlacedBy(UUID placedBy) {
		this.placedByUUID = placedBy;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		// Vanilla uses the type parameter to indicate which type of tile entity (command block, skull, or beacon?) is receiving the packet, but it seems like Forge has overridden this behavior
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}

	@Override
	public AABB getRenderBoundingBox() {
		// Room for the area preview
		return new AABB(getBlockPos()).inflate(10);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		if (this instanceof AreaAffectingBE areaAffectingBE)
			areaAffectingBE.getAreaAffectingData().area = null; //Clear this cache when a packet comes in, so it can redraw properly if the area was changed
	}

	public void markDirtyClient() {
		setChanged();
		if (level != null) {
			BlockState state = level.getBlockState(getBlockPos());
			level.sendBlockUpdated(getBlockPos(), state, state, 3);
		}
	}

	@Override
	public void clearContent() {
		ItemStackHandler handler = getMachineHandler();

		for (int i = 0; i < handler.getSlots(); i++) {
			handler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this instanceof FilterableBE filterableBE) {
			filterableBE.getFilterData().filterCache.clear();
			filterableBE.getFilterData().entityCache.clear();
		}
	}

	public FilterData getDefaultFilterData() {
		return new FilterData();
	}

	public RedstoneControlData getDefaultRedstoneData() {
		return new RedstoneControlData();
	}

	public AreaAffectingData getDefaultAreaData(AreaAffectingBE areaAffectingBE) {
		return areaAffectingBE.getDefaultAreaData(getBlockState().getValue(BlockStateProperties.FACING));
	}

	public boolean isDefaultSettings() {
		if (tickSpeed != 20)
			return false;
		if (direction != 0)
			return false;
		if (this instanceof AreaAffectingBE areaAffectingBE && !areaAffectingBE.getAreaAffectingData().equals(getDefaultAreaData(areaAffectingBE)))
			return false;
		if (this instanceof FilterableBE filterableBE && !filterableBE.getFilterData().equals(getDefaultFilterData()))
			return false;
		if (this instanceof FilterableBE filterableBE) {
			FilterBasicHandler filteredItems = filterableBE.getFilterHandler();
			for (int i = 0; i < filteredItems.getSlots(); i++) {
				if (!filteredItems.getStackInSlot(i).isEmpty())
					return false;
			}
		}
		if (this instanceof PoweredMachineBE poweredMachineBE && poweredMachineBE.getEnergyStored() > 0)
			return false;
		if (this instanceof RedstoneControlledBE redstoneControlledBE && !redstoneControlledBE.getRedstoneControlData().equals(getDefaultRedstoneData()))
			return false;
		return true;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("tickspeed", tickSpeed);
		if (placedByUUID != null)
			tag.putUUID("placedBy", placedByUUID);
		tag.putInt("direction", direction);
		if (MACHINE_SLOTS > 0)
			tag.put("machineHandler", getMachineHandler().serializeNBT());
		if (this instanceof AreaAffectingBE areaAffectingBE)
			areaAffectingBE.saveAreaSettings(tag);
		if (this instanceof FilterableBE filterableBE) {
			filterableBE.saveFilterSettings(tag);
			tag.put("filterHandler", filterableBE.getFilterHandler().serializeNBT());
		}
		if (this instanceof RedstoneControlledBE redstoneControlledBE)
			redstoneControlledBE.saveRedstoneSettings(tag);
	}

	@Override
	public void load(CompoundTag tag) {
		if (tag.contains("direction"))
			direction = tag.getInt("direction");
		if (tag.contains("tickspeed"))
			tickSpeed = tag.getInt("tickspeed");
		if (tag.contains("placedBy"))
			placedByUUID = tag.getUUID("placedBy");
		if (tag.contains("machineHandler"))
			getMachineHandler().deserializeNBT(tag.getCompound("machineHandler"));
		if (this instanceof AreaAffectingBE areaAffectingBE)
			areaAffectingBE.loadAreaSettings(tag);
		if (this instanceof FilterableBE filterableBE) {
			filterableBE.loadFilterSettings(tag);
			if (tag.contains("filterHandler"))
				filterableBE.getFilterHandler().deserializeNBT(tag.getCompound("filterHandler"));
		}
		if (this instanceof RedstoneControlledBE redstoneControlledBE)
			redstoneControlledBE.loadRedstoneSettings(tag);
		super.load(tag);
	}
}
