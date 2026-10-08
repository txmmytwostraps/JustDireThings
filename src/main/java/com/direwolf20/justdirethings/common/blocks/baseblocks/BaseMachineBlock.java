package com.direwolf20.justdirethings.common.blocks.baseblocks;

import com.direwolf20.justdirethings.common.blockentities.basebe.AreaAffectingBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.RedstoneControlledBE;
import com.direwolf20.justdirethings.common.items.FerricoreWrench;
import com.direwolf20.justdirethings.common.items.MachineSettingsCopier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseMachineBlock extends Block implements EntityBlock {
	public static final String DROP_DATA = "JustDiresBEData";

	public BaseMachineBlock(Properties properties) {
		super(properties);
	}

	public static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
		return false;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(world, pos, state, entity, stack);
		if (!world.isClientSide && entity instanceof Player player) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BaseMachineBE baseMachineBE) {
				CompoundTag compound = stack.getTagElement(DROP_DATA);
				if (compound != null && !compound.isEmpty())
					blockEntity.load(compound.copy());
				baseMachineBE.setPlacedBy(player.getUUID());
			}
		}
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (hand != InteractionHand.MAIN_HAND)
			return InteractionResult.PASS;
		if (level.isClientSide)
			return InteractionResult.SUCCESS;

		ItemStack itemStack = player.getMainHandItem();
		if (itemStack.getItem() instanceof MachineSettingsCopier || itemStack.getItem() instanceof FerricoreWrench)
			return InteractionResult.PASS;

		BlockEntity te = level.getBlockEntity(blockPos);
		if (!isValidBE(te))
			return InteractionResult.FAIL;

		openMenu(player, blockPos);

		return InteractionResult.SUCCESS;
	}

	public abstract void openMenu(Player player, BlockPos blockPos);

	public abstract boolean isValidBE(BlockEntity blockEntity);

	/**
	 * Fills or empties a held fluid container. fillPartialContainers also tops up part-full ones.
	 */
	protected boolean useFluidContainer(ItemStack itemStack, Level level, BlockPos blockPos, Player player, InteractionHand hand, Direction side, boolean fillPartialContainers) {
		IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		if (fluidHandlerItem == null)
			return false;
		BlockEntity blockEntity = level.getBlockEntity(blockPos);
		IFluidHandler cap = blockEntity == null ? null : blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).orElse(null);
		if (cap == null)
			return false;
		boolean fillContainer = fillPartialContainers
				? fluidHandlerItem.getFluidInTank(0).getAmount() < fluidHandlerItem.getTankCapacity(0) && !cap.getFluidInTank(0).isEmpty()
				: fluidHandlerItem.getFluidInTank(0).isEmpty();
		if (fillContainer) {
			FluidStack testStack = cap.drain(fluidHandlerItem.getTankCapacity(0), IFluidHandler.FluidAction.SIMULATE);
			if (testStack.getAmount() > 0) {
				int amtFit = fluidHandlerItem.fill(testStack, IFluidHandler.FluidAction.SIMULATE);
				if (amtFit > 0) {
					FluidStack extractedStack = cap.drain(amtFit, IFluidHandler.FluidAction.EXECUTE);
					fluidHandlerItem.fill(extractedStack, IFluidHandler.FluidAction.EXECUTE);
					if (itemStack.getItem() instanceof BucketItem)
						player.setItemSlot(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, fluidHandlerItem.getContainer());
					level.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1F, 1.0F);
					return true;
				}
			}
		} else {
			FluidStack fluidStack = fluidHandlerItem.getFluidInTank(0);
			int insertAmt = cap.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
			if (insertAmt > 0) {
				FluidStack extractedStack = fluidHandlerItem.drain(insertAmt, IFluidHandler.FluidAction.EXECUTE);
				if (!extractedStack.isEmpty()) {
					cap.fill(extractedStack, IFluidHandler.FluidAction.EXECUTE);
					if (itemStack.getItem() instanceof BucketItem)
						player.setItemSlot(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, fluidHandlerItem.getContainer());
					level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1F, 1.0F);
					return true;
				}
			}
		}
		return false;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return (lvl, pos, blockState, t) -> {
				if (t instanceof BaseMachineBE tile) {
					tile.tickClient();
				}
			};
		}
		return (lvl, pos, blockState, t) -> {
			if (t instanceof BaseMachineBE tile) {
				tile.tickServer();
			}
		};
	}

	public void neighborChanged(BlockState blockState, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(blockState, level, pos, blockIn, fromPos, isMoving);
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof RedstoneControlledBE redstoneControlledBE) {
			redstoneControlledBE.getRedstoneControlData().checkedRedstone = false;
		}
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @javax.annotation.Nullable Direction direction) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof RedstoneControlledBE) {
			return true;
		}
		return super.canConnectRedstone(state, level, pos, direction);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() != this) {
			BlockEntity blockEntity = worldIn.getBlockEntity(pos);
			if (blockEntity instanceof BaseMachineBE baseMachineBE) {
				IItemHandler iItemHandler = baseMachineBE.getMachineHandler();
				for (int i = 0; i < iItemHandler.getSlots(); ++i) {
					Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), iItemHandler.getStackInSlot(i));
				}
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = super.getDrops(state, builder); // Get default drops
		BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);

		if (blockEntity instanceof BaseMachineBE baseMachineBE && !baseMachineBE.isDefaultSettings()) {
			ItemStack itemStack = new ItemStack(Item.byBlock(this));
			CompoundTag compoundTag = new CompoundTag();
			saveDropData(baseMachineBE, compoundTag);
			if (!compoundTag.isEmpty()) {
				itemStack.addTagElement(DROP_DATA, compoundTag);
			}
			drops.clear(); // Clear any default drops
			drops.add(itemStack); // Add your custom item stack with NBT data
		}

		return drops;
	}

	protected void saveDropData(BaseMachineBE baseMachineBE, CompoundTag compoundTag) {
		baseMachineBE.saveAdditional(compoundTag);
		// Items drop in onRemove. Keeping them here would dupe them in explosions.
		compoundTag.remove("machineHandler");
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(BlockStateProperties.FACING, rotation.rotate(state.getValue(BlockStateProperties.FACING)));
	}

	public BlockState direRotate(BlockState blockState, LevelAccessor level, BlockPos pos, Rotation direction) {
		BlockState newState = direRotate(blockState, direction);
		if (level.getBlockEntity(pos) instanceof AreaAffectingBE areaAffectingBE) {
			areaAffectingBE.handleRotate(blockState.getValue(BlockStateProperties.FACING), newState.getValue(BlockStateProperties.FACING));
		}
		return newState;
	}

	public BlockState direRotate(BlockState state, Rotation rotation) {
		DirectionProperty prop = BlockStateProperties.FACING;
		Comparable<?> currentValue = state.getValue(prop);
		List<Direction> directions = prop.getPossibleValues().stream().toList();
		int currentDirectionIndex = directions.indexOf(currentValue);
		int nextDirectionIndex = (currentDirectionIndex + 1) % directions.size();
		Direction nextDirection = directions.get(nextDirectionIndex);
		return state.setValue(prop, nextDirection);
	}
}
