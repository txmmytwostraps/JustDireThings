package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.FluidContainerData;
import com.direwolf20.justdirethings.common.blockentities.basebe.FluidMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.PoweredMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.PoweredMachineContainerData;
import com.direwolf20.justdirethings.common.blockentities.basebe.RedstoneControlledBE;
import com.direwolf20.justdirethings.common.capabilities.EnergyStorageNoReceive;
import com.direwolf20.justdirethings.common.capabilities.GeneratorFluidItemHandler;
import com.direwolf20.justdirethings.common.capabilities.JustDireFluidTank;
import com.direwolf20.justdirethings.common.fluids.basefluids.RefinedFuel;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.BlockEnergyCache;
import com.direwolf20.justdirethings.util.interfacehelpers.RedstoneControlData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemStackHandler;

public class GeneratorFluidT1BE extends BaseMachineBE implements RedstoneControlledBE, PoweredMachineBE, FluidMachineBE {
	public RedstoneControlData redstoneControlData = new RedstoneControlData();
	public final PoweredMachineContainerData poweredMachineData;
	public final FluidContainerData fluidContainerData;
	private final BlockEnergyCache energyHandlers = new BlockEnergyCache();
	private final EnergyStorageNoReceive energyStorage;
	private final JustDireFluidTank fluidTank;

	public GeneratorFluidT1BE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		MACHINE_SLOTS = 1;
		energyStorage = new EnergyStorageNoReceive(getMaxEnergy());
		fluidTank = new JustDireFluidTank(getMaxMB(), fluidStack -> fluidStack.getFluid() instanceof RefinedFuel);
		fluidContainerData = new FluidContainerData(this);
		poweredMachineData = new PoweredMachineContainerData(this);
	}

	public GeneratorFluidT1BE(BlockPos pPos, BlockState pBlockState) {
		this(Registration.GeneratorFluidT1BE.get(), pPos, pBlockState);
	}

	@Override
	public int getMaxMB() {
		return 4000;
	}

	@Override
	public JustDireFluidTank getFluidTank() {
		return fluidTank;
	}

	@Override
	public ContainerData getFluidContainerData() {
		return fluidContainerData;
	}

	@Override
	public RedstoneControlData getRedstoneControlData() {
		return redstoneControlData;
	}

	@Override
	public BlockEntity getBlockEntity() {
		return this;
	}

	@Override
	public ContainerData getContainerData() {
		return poweredMachineData;
	}

	@Override
	public EnergyStorageNoReceive getEnergyStorage() {
		return energyStorage;
	}

	@Override
	public int getStandardEnergyCost() {
		return 0;
	}

	@Override
	public void tickClient() {
	}

	@Override
	public void tickServer() {
		super.tickServer();
		handleItemStack();
		doGenerate();
		providePowerAdjacent();
	}

	public ItemStack getItemStack() {
		return getMachineHandler().getStackInSlot(0);
	}

	@Override
	public ItemStackHandler getMachineHandler() {
		if (machineHandler == null) {
			machineHandler = new GeneratorFluidItemHandler(MACHINE_SLOTS) {
				@Override
				protected void onContentsChanged(int slot) {
					setChanged();
				}
			};
		}
		return machineHandler;
	}

	public boolean isStackValid(ItemStack itemStack) {
		if (itemStack.isEmpty())
			return false;
		IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		if (fluidHandlerItem == null)
			return false;
		FluidStack fluidStack = fluidHandlerItem.drain(1000, IFluidHandler.FluidAction.SIMULATE);
		if (fluidStack.getAmount() == 0)
			return false;
		if (!getFluidStack().isEmpty() && getFluidStack().getFluid() != fluidStack.getFluid())
			return false;
		if (!(getFluidTank().isFluidValid(fluidStack)))
			return false;
		return true;
	}

	public void handleItemStack() {
		if (isFull()) return;
		ItemStack itemStack = getItemStack();
		if (!isStackValid(itemStack)) return;
		IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		FluidStack testExtract = fluidHandlerItem.drain(1000, IFluidHandler.FluidAction.SIMULATE);
		int insertAmt = getFluidTank().fill(testExtract, IFluidHandler.FluidAction.SIMULATE);
		if (insertAmt > 0) {
			FluidStack extractedStack = fluidHandlerItem.drain(insertAmt, IFluidHandler.FluidAction.EXECUTE);
			getFluidTank().fill(extractedStack, IFluidHandler.FluidAction.EXECUTE);
			if (itemStack.getItem() instanceof BucketItem)
				getMachineHandler().setStackInSlot(0, fluidHandlerItem.getContainer());
		}
	}

	@Override
	public int insertEnergy(int power, boolean simulate) {
		return energyStorage.forceReceiveEnergy(power, simulate);
	}

	public IEnergyStorage getHandler(Direction direction) {
		return energyHandlers.get(level, getBlockPos().relative(direction), direction.getOpposite());
	}

	public void providePowerAdjacent() {
		if (getEnergyStorage().getEnergyStored() <= 0) return; //Don't bother if we're empty!
		for (Direction direction : Direction.values()) {
			IEnergyStorage iEnergyStorage = getHandler(direction);
			if (iEnergyStorage == null) continue;
			int amtFit = iEnergyStorage.receiveEnergy(getFEOutputPerTick() * 10, true);
			if (amtFit <= 0) continue;
			int extractAmt = extractEnergy(amtFit, false);
			iEnergyStorage.receiveEnergy(extractAmt, false);
		}
	}

	public void doGenerate() {
		if (!isActiveRedstone() || getFluidStack().isEmpty()) return;
		int fePerFuelTick = getFePerFuelTick();
		boolean canInsertEnergy = insertEnergy(fePerFuelTick, true) == fePerFuelTick;
		if (fePerFuelTick == 0 || !canInsertEnergy) return; //Don't burn if the buffer is full
		FluidStack extractedStack = getFluidTank().drain(1, IFluidHandler.FluidAction.EXECUTE);
		if (extractedStack.getAmount() == 0) return;

		//At this point we extracted some fuel!
		insertEnergy(fePerFuelTick, false); //Receive energy

		setChanged();
	}

	@Override
	public void handleTicks() {
		//NoOp
	}

	@Override
	public boolean canRun() {
		return true;
	}

	@Override
	public int getMaxEnergy() {
		return Config.GENERATOR_FLUID_T1_MAX_FE.get();
	}

	public int getFEOutputPerTick() {
		return Config.GENERATOR_FLUID_T1_FE_PER_TICK.get();
	}

	public int getFePerFuelTick() {
		return getFluidTank().getFluid().getFluid() instanceof RefinedFuel refinedFuel ? refinedFuel.fePerMb() : 0;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("energy", energyStorage.getEnergyStored());
		tag.put("fluidTank", fluidTank.serializeToNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("energy"))
			energyStorage.setEnergy(tag.getInt("energy"));
		if (tag.contains("fluidTank"))
			fluidTank.deserializeFromNBT(tag.getCompound("fluidTank"));
	}
}
