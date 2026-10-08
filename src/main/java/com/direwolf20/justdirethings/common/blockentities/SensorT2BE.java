package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.common.blockentities.basebe.AreaAffectingBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.PoweredMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.PoweredMachineContainerData;
import com.direwolf20.justdirethings.common.capabilities.MachineEnergyStorage;
import com.direwolf20.justdirethings.common.containers.handlers.FilterBasicHandler;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.interfacehelpers.AreaAffectingData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SensorT2BE extends SensorT1BE implements AreaAffectingBE, PoweredMachineBE {
	public AreaAffectingData areaAffectingData = new AreaAffectingData(getBlockState().getValue(BlockStateProperties.FACING));
	public final PoweredMachineContainerData poweredMachineData;
	private final MachineEnergyStorage energyStorage;
	private final FilterBasicHandler filterHandler;

	public SensorT2BE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		poweredMachineData = new PoweredMachineContainerData(this);
		energyStorage = new MachineEnergyStorage(getMaxEnergy());
		filterHandler = new FilterBasicHandler(9);
	}

	public SensorT2BE(BlockPos pPos, BlockState pBlockState) {
		this(Registration.SensorT2BE.get(), pPos, pBlockState);
	}

	@Override
	public FilterBasicHandler getFilterHandler() {
		return filterHandler;
	}

	@Override
	public AreaAffectingData getAreaAffectingData() {
		return areaAffectingData;
	}

	@Override
	public PoweredMachineContainerData getContainerData() {
		return poweredMachineData;
	}

	@Override
	public MachineEnergyStorage getEnergyStorage() {
		return energyStorage;
	}

	@Override
	public int getStandardEnergyCost() {
		return 2;
	}

	public int getEnergyCost() {
		AABB aabb = getAABB(getBlockPos());
		int width = (int) Math.abs(Math.floor(aabb.maxX) - Math.floor(aabb.minX));
		int height = (int) Math.abs(Math.floor(aabb.maxY) - Math.floor(aabb.minY));
		int depth = (int) Math.abs(Math.floor(aabb.maxZ) - Math.floor(aabb.minZ));
		return (width * height * depth) * getStandardEnergyCost();
	}

	@Override
	public boolean canSense() {
		int cost = getEnergyCost();
		return extractEnergy(cost, false) >= cost; //Really extract because if we pass this we'll be scanning!
	}

	@Override
	public List<Entity> findEntities(AABB aabb) {
		return level.getEntitiesOfClass(Entity.class, getAABB(getBlockPos()), this::isValidEntity);
	}

	@Override
	public AABB getAABB() {
		return getAABB(getBlockPos());
	}

	@Override
	public List<BlockPos> findPositions() {
		AABB area = getAABB(getBlockPos());
		return BlockPos.betweenClosedStream((int) area.minX, (int) area.minY, (int) area.minZ, (int) area.maxX - 1, (int) area.maxY - 1, (int) area.maxZ - 1)
				.filter(this::isBlockPosValid)
				.map(BlockPos::immutable)
				.sorted(Comparator.comparingDouble(x -> x.distSqr(getBlockPos())))
				.collect(Collectors.toList());
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("energy", energyStorage.getEnergyStored());
	}

	@Override
	public void load(CompoundTag tag) {
		if (tag.contains("energy"))
			energyStorage.setEnergy(tag.getInt("energy"));
		super.load(tag);
	}
}
