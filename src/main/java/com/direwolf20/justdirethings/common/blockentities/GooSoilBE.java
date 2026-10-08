package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GooSoilBE extends BlockEntity {
	private NBTHelpers.BoundInventory boundInventory;
	protected LazyOptional<IItemHandler> attachedInventory = LazyOptional.empty();

	public GooSoilBE(BlockPos pos, BlockState state) {
		super(Registration.GooSoilBE.get(), pos, state);
	}

	public void bindInventory(NBTHelpers.BoundInventory boundInventory) {
		this.boundInventory = boundInventory;
		this.setChanged();
	}

	public IItemHandler getAttachedInventory(ServerLevel serverLevel) {
		if (boundInventory == null) return null;
		if (!attachedInventory.isPresent()) {
			ServerLevel boundLevel = serverLevel.getServer().getLevel(boundInventory.globalPos().dimension());
			if (boundLevel == null) return null;
			BlockPos boundPos = boundInventory.globalPos().pos();
			if (!boundLevel.isLoaded(boundPos)) return null; //Don't load a chunk just for drops
			BlockEntity blockEntity = boundLevel.getBlockEntity(boundPos);
			if (blockEntity == null) return null;
			attachedInventory = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, boundInventory.direction());
		}
		return attachedInventory.orElse(null);
	}

	public void invalidateHandler() {
		attachedInventory = LazyOptional.empty();
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (boundInventory != null) {
			tag.put("boundinventory", NBTHelpers.BoundInventory.toNBT(boundInventory));
		}
	}

	@Override
	public void load(CompoundTag tag) {
		if (tag.contains("boundinventory"))
			boundInventory = NBTHelpers.BoundInventory.fromNBT(tag.getCompound("boundinventory"));
		super.load(tag);
	}
}
