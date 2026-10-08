package com.direwolf20.justdirethings.common.blocks;

import com.direwolf20.justdirethings.common.blockentities.GeneratorFluidT1BE;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.GeneratorFluidT1Container;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class GeneratorFluidT1 extends BaseMachineBlock {
	static final VoxelShape SHAPE = Stream.of(
			Block.box(2, 11, 2, 14, 14, 14),
			Block.box(0, 0, 0, 16, 11, 16),
			Block.box(0, 14, 0, 16, 16, 16)
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public GeneratorFluidT1() {
		super(Properties.of(Material.METAL, MaterialColor.NONE)
				.sound(SoundType.METAL)
				.strength(2.0f)
				.isRedstoneConductor(BaseMachineBlock::never)
		);
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide && useFluidContainer(player.getItemInHand(hand), level, blockPos, player, hand, hit.getDirection(), false))
			return InteractionResult.SUCCESS;
		return super.use(blockState, level, blockPos, player, hand, hit);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GeneratorFluidT1BE(pos, state);
	}

	@Override
	public void openMenu(Player player, BlockPos blockPos) {
		NetworkHooks.openGui((ServerPlayer) player, new SimpleMenuProvider(
				(windowId, playerInventory, playerEntity) -> new GeneratorFluidT1Container(windowId, playerInventory, blockPos), new TranslatableComponent("")), (buf -> {
			buf.writeBlockPos(blockPos);
		}));
	}

	@Override
	public boolean isValidBE(BlockEntity blockEntity) {
		return blockEntity instanceof GeneratorFluidT1BE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	//Override BaseMachineBlock being rotatable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state;
	}

	@Override
	public BlockState direRotate(BlockState blockState, LevelAccessor level, BlockPos pos, Rotation direction) {
		return blockState;
	}
}
