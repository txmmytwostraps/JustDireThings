package com.direwolf20.justdirethings.common.blocks.gooblocks;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class GooPatternBlock extends Block {
	public static final IntegerProperty GOOSTAGE = IntegerProperty.create("goostage", 0, 11);

	public GooPatternBlock() {
		super(Properties.of(Material.CLAY, MaterialColor.NONE)
				.sound(SoundType.FUNGUS)
				.strength(2.0f)
				.noOcclusion()
		);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(GOOSTAGE, 0);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(GOOSTAGE);
	}
}
