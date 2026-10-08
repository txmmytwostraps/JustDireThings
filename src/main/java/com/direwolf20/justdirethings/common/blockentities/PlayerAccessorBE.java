package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.containers.handlers.PlayerHandler;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerAccessorBE extends BaseMachineBE {
	public ServerPlayer serverPlayer;
	public HashMap<Direction, PlayerHandler> playerHandlers = new HashMap<>();
	public HashMap<Direction, Integer> sidedInventoryTypes = new HashMap<>();
	boolean checkedPlayer = false;
	private final HashMap<Direction, LazyOptional<IItemHandler>> playerHandlerCaps = new HashMap<>();
	private LazyOptional<IItemHandler> clientHandler = LazyOptional.empty();

	public PlayerAccessorBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	// Read live. The saved tick speed would go stale.
	@Override
	public int getTickSpeed() {
		return Config.PLAYER_ACCESSOR_VALIDATION_TIME.get(); //This controls how often validatePlayer() runs.  Don't worry if the player leaves before this checks, because the itemhandler will prevent insertion if they do!
	}

	@Override
	public void tickServer() {
		super.tickServer();
		if (canRun()) { //Do this every 100 ticks
			checkedPlayer = false;
			validatePlayer();
		}
	}

	public void updateSidedInventory(Direction direction, int type) {
		sidedInventoryTypes.put(direction, type);
		clearCache();
		markDirtyClient();
	}

	public PlayerAccessorBE(BlockPos pPos, BlockState pBlockState) {
		this(Registration.PlayerAccessorBE.get(), pPos, pBlockState);
	}

	public void clearCache() {
		this.playerHandlers.clear();
		this.serverPlayer = null;
		if (level != null) {
			updateServerPlayer(); //Since we cleared out the old player, check to see if they still exist, like a dimension change or something
		}
		markDirtyClient();
	}

	/**
	 * Check if the player is in an invalid dimension.
	 */
	public boolean isValidDim(ServerPlayer serverPlayer) {
		if (serverPlayer == null || serverPlayer.isRemoved())
			return false;
		if (!Config.PLAYER_ACCESSOR_DIMENSIONAL_BLACKLISTING.get())
			return true;
		List<? extends String> blacklist = Config.PLAYER_ACCESSOR_BLACKLISTED_DIMENSIONS.get();
		return !blacklist.contains(serverPlayer.level.dimension().location().toString());
	}

	/**
	 * Clear the caches, and re-locate the player object if necessary (Like player logged out, etc)
	 */
	public void validatePlayer() {
		if (getServerPlayer() != null && (serverPlayer.isRemoved() || !isValidDim(serverPlayer))) {
			clearCache();
		}
	}

	/**
	 * If we have a cached player, return it, otherwise look for the player object on the server
	 */
	public ServerPlayer getServerPlayer() {
		if (serverPlayer == null && !checkedPlayer) //Only allow this to recheck every 100 ticks
			updateServerPlayer();
		return serverPlayer;
	}

	/**
	 * Find the player on the server, if the one we have cached is either null or removed.
	 */
	public void updateServerPlayer() {
		checkedPlayer = true;
		if (this.placedByUUID == null) {
			serverPlayer = null;
			return;
		}
		if (!isValidDim(level.getServer().getPlayerList().getPlayer(this.placedByUUID))) {
			serverPlayer = null;
			return;
		}
		if (serverPlayer == null || serverPlayer.isRemoved()) {
			boolean wasNull = serverPlayer == null;
			serverPlayer = level.getServer().getPlayerList().getPlayer(this.placedByUUID);
			if ((wasNull && serverPlayer != null) || (!wasNull && serverPlayer == null))
				markDirtyClient();
		}
	}

	/**
	 * Return the player handler for the specified side of this block
	 */
	public PlayerHandler getPlayerHandler(Direction side) {
		if (getServerPlayer() == null) //If our player does not exist
			return null;
		if (!playerHandlers.containsKey(side)) { //If we don't have a cache of the side, create one using the player we have cached
			playerHandlers.put(side, new PlayerHandler(serverPlayer, PlayerHandler.InventoryType.values()[sidedInventoryTypes.getOrDefault(side, 0)]));
		} else {
			if (playerHandlers.get(side).player.isRemoved()) { //If the player handler's player has been removed (Logged off) re-validate
				validatePlayer();
				if (serverPlayer == null)
					return null;
				playerHandlers.put(side, new PlayerHandler(serverPlayer, PlayerHandler.InventoryType.values()[sidedInventoryTypes.getOrDefault(side, 0)]));
			}
		}
		return playerHandlers.get(side);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (level.isClientSide) { //The player only exists on the server
				if (!clientHandler.isPresent())
					clientHandler = LazyOptional.of(() -> new ItemStackHandler(1));
				return clientHandler.cast();
			}
			PlayerHandler playerHandler = getPlayerHandler(side);
			if (playerHandler == null)
				return LazyOptional.empty();
			return playerHandlerCaps.computeIfAbsent(side, s -> LazyOptional.of(() -> playerHandler)).cast();
		}
		return super.getCapability(cap, side);
	}

	/**
	 * Makes anything holding our handlers look them up again.
	 */
	private void invalidatePlayerHandlerCaps() {
		List<LazyOptional<IItemHandler>> handlerCaps = new ArrayList<>(playerHandlerCaps.values());
		playerHandlerCaps.clear();
		handlerCaps.forEach(LazyOptional::invalidate);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		clientHandler.invalidate();
		invalidatePlayerHandlerCaps();
	}

	@Override
	public boolean isDefaultSettings() {
		return true;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		for (Direction direction : Direction.values()) {
			tag.putInt("sidedInventory_" + direction.ordinal(), sidedInventoryTypes.getOrDefault(direction, 0));
		}
	}

	@Override
	public void load(CompoundTag tag) {
		for (Direction direction : Direction.values()) {
			sidedInventoryTypes.put(direction, tag.getInt("sidedInventory_" + direction.ordinal()));
		}
		super.load(tag);
	}

	@Override
	public void markDirtyClient() {
		invalidatePlayerHandlerCaps();
		super.markDirtyClient();
	}
}
