package com.direwolf20.justdirethings.common.blocks.soil;

import com.direwolf20.justdirethings.common.blockentities.GooSoilBE;
import com.direwolf20.justdirethings.common.items.interfaces.Helpers;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.CactusBlock.AGE;

public class GooSoilBase extends FarmBlock {
	public GooSoilBase() {
		super(Properties.of(Material.CLAY, MaterialColor.NONE)
				.sound(SoundType.GRAVEL)
				.strength(2.0f)
				.randomTicks()
		);
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		BlockState blockstate = pLevel.getBlockState(pPos.above());
		if (blockstate.is(Blocks.BAMBOO) || blockstate.is(Blocks.CACTUS) || blockstate.is(Blocks.BAMBOO_SAPLING))
			return true;
		return !blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, IPlantable plantable) {
		if (facing != Direction.UP) return false;
		BlockState plant = plantable.getPlant(level, soilPosition.relative(facing));
		if (plant.getBlock() instanceof CactusBlock)
			return true;

		if (plant.getBlock() instanceof SugarCaneBlock)
			return true;

		if (plant.getBlock() == Blocks.BAMBOO || plant.getBlock() == Blocks.BAMBOO_SAPLING)
			return true;

		if (plant.getBlock() == Blocks.NETHER_WART)
			return true;

		if (plant.getBlock() instanceof BushBlock)
			return true;

		return super.canSustainPlant(state, level, soilPosition, facing, plantable);
	}

	/**
	 * Moist soil speeds up crops like moist farmland.
	 */
	@Override
	public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(MOISTURE) > 0;
	}

	@Override
	public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
		pEntity.causeFallDamage(pFallDistance, 1.0F, DamageSource.FALL);
	}

	public static void bonemealMe(ServerLevel pLevel, BlockPos pPos) {
		BlockState soilState = pLevel.getBlockState(pPos);
		if (!(soilState.getBlock() instanceof GooSoilBase) || soilState.getValue(MOISTURE) < 7)
			return;
		BlockPos cropPos = pPos.above();
		BlockState crop = pLevel.getBlockState(cropPos);
		BlockState crop1 = null;
		if (crop.getBlock() instanceof CropBlock cropblock) {
			if (!cropblock.isMaxAge(crop)) {
				crop1 = cropblock.getStateForAge(crop.getValue(cropblock.getAgeProperty()) + 1);
			}
		} else if (crop.getBlock() instanceof StemBlock) {
			int k = crop.getValue(StemBlock.AGE);
			if (k < 7) {
				crop1 = crop.setValue(StemBlock.AGE, k + 1);
			}
		} else if (crop.is(Blocks.SWEET_BERRY_BUSH) || crop.is(Blocks.NETHER_WART)) {
			int j = crop.getValue(SweetBerryBushBlock.AGE);
			if (j < 3) {
				crop1 = crop.setValue(SweetBerryBushBlock.AGE, j + 1);
			}
		} else if (crop.getBlock() instanceof SugarCaneBlock || crop.getBlock() instanceof CactusBlock) { //Bamboo is weird, Cactus and Sugarcane are at least consistent
			BlockPos blockpos = cropPos.above();
			int j = crop.getValue(AGE);
			if (j == 15) {
				pLevel.setBlockAndUpdate(blockpos, crop.getBlock().defaultBlockState());
				BlockState blockstate = crop.setValue(AGE, Integer.valueOf(0));
				pLevel.setBlock(cropPos, blockstate, 4);
				blockstate.neighborChanged(pLevel, blockpos, crop.getBlock(), cropPos, false);
			} else {
				pLevel.setBlock(cropPos, crop.setValue(AGE, Integer.valueOf(j + 1)), 4);
			}
		}
		if (crop1 != null) {
			pLevel.setBlockAndUpdate(cropPos, crop1);
		}
	}

	public static List<ItemStack> harvestStemCrop(ServerLevel pLevel, BlockPos cropPos, BlockState crop) {
		List<ItemStack> drops = new ArrayList<>();
		if (crop.getBlock() instanceof AttachedStemBlock attachedStemBlock) {
			for (Direction direction : Direction.values()) {
				BlockPos testPos = cropPos.relative(direction);
				BlockState fruitState = pLevel.getBlockState(testPos);
				//The stem's fruit is private, so ask the fruit
				if (fruitState.getBlock() instanceof StemGrownBlock fruit && fruit.getAttachedStem() == attachedStemBlock) {
					BlockEntity blockEntity = pLevel.getBlockEntity(cropPos);
					drops.addAll(Block.getDrops(fruitState, pLevel, testPos, blockEntity));
					pLevel.destroyBlock(testPos, false);
				}
			}
		}
		return drops;
	}

	public static List<ItemStack> harvest2TallCrop(ServerLevel pLevel, BlockPos cropPos, BlockState crop) {
		List<ItemStack> drops = new ArrayList<>();
		if (crop.getBlock() instanceof SugarCaneBlock || crop.getBlock() instanceof CactusBlock || crop.is(Blocks.BAMBOO)) {
			List<BlockPos> posToCheck = new ArrayList<>();
			for (int i = 0; i < 10; i++) { //In case it grew a lot since last check
				BlockPos pos = cropPos.above(i);
				if (pLevel.getBlockState(pos).is(crop.getBlock()))
					posToCheck.add(pos);
				else
					break;
			}
			for (int i = posToCheck.size() - 1; i >= 0; i--) {
				BlockPos clearPos = posToCheck.get(i);
				BlockEntity blockEntity = pLevel.getBlockEntity(clearPos);
				drops.addAll(Block.getDrops(pLevel.getBlockState(clearPos), pLevel, clearPos, blockEntity));
				pLevel.destroyBlock(clearPos, false);
			}
		}
		return drops;
	}

	public static List<ItemStack> harvestCrop(ServerLevel pLevel, BlockPos cropPos, BlockState crop) {
		List<ItemStack> drops = new ArrayList<>();
		if (crop.getBlock() instanceof BushBlock) {
			BlockEntity blockEntity = pLevel.getBlockEntity(cropPos);
			List<ItemStack> potentialDrops = Block.getDrops(crop, pLevel, cropPos, blockEntity);
			if (potentialDrops.isEmpty())
				return drops;
			if (potentialDrops.size() > 1 || potentialDrops.get(0).getCount() > 1) {
				BlockState placeState = Blocks.AIR.defaultBlockState();
				drops.addAll(potentialDrops);
				for (ItemStack drop : drops) {
					if (drop.getItem() instanceof BlockItem blockItem) {
						placeState = blockItem.getBlock().defaultBlockState();
						drop.shrink(1);
						break;
					}
				}
				pLevel.destroyBlock(cropPos, false);
				pLevel.setBlockAndUpdate(cropPos, placeState);
			}
		}
		return drops;
	}

	public static void teleportDrops(ServerLevel pLevel, BlockPos pPos, List<ItemStack> drops, BlockPos cropPos) {
		BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
		if (blockEntity instanceof GooSoilBE gooSoilBE) {
			IItemHandler handler = gooSoilBE.getAttachedInventory(pLevel);
			if (handler != null) {
				Helpers.teleportDrops(drops, handler);
				if (drops.isEmpty()) //Only spawn particles if we teleported everything - granted this isn't perfect, but way better than exhaustive testing
					ToggleableTool.teleportParticles(pLevel, cropPos, 5);
			}
		}
	}

	public static void dropDrops(ServerLevel pLevel, List<ItemStack> drops, BlockPos cropPos) {
		for (ItemStack drop : drops) {
			ItemEntity itemEntity = new ItemEntity(pLevel, cropPos.getX(), cropPos.getY(), cropPos.getZ(), drop);
			itemEntity.lifespan = 40;
			pLevel.addFreshEntity(itemEntity);
		}
	}

	public static void autoHarvest(ServerLevel pLevel, BlockPos pPos) {
		BlockPos cropPos = pPos.above();
		BlockState crop = pLevel.getBlockState(cropPos);
		if (crop.getBlock() instanceof BushBlock) {
			if (crop.getBlock() instanceof AttachedStemBlock) {
				List<ItemStack> drops = harvestStemCrop(pLevel, cropPos, crop);
				if (!drops.isEmpty()) {
					teleportDrops(pLevel, pPos, drops, cropPos);
					dropDrops(pLevel, drops, cropPos);
				}
			} else if (crop.getBlock() instanceof StemBlock) {
				//No-Op - Stem Blocks are fully grown without an attached fruit, so ignore them
			} else {
				List<ItemStack> drops = harvestCrop(pLevel, cropPos, crop);
				if (!drops.isEmpty()) {
					teleportDrops(pLevel, pPos, drops, cropPos);
					dropDrops(pLevel, drops, cropPos);
				}
			}
		} else if (crop.getBlock() instanceof SugarCaneBlock || crop.getBlock() instanceof CactusBlock || crop.is(Blocks.BAMBOO)) {
			BlockPos secondPos = cropPos.above();
			BlockState secondState = pLevel.getBlockState(secondPos);
			if (secondState.is(crop.getBlock())) {
				List<ItemStack> drops = harvest2TallCrop(pLevel, secondPos, secondState);
				if (!drops.isEmpty()) {
					teleportDrops(pLevel, pPos, drops, secondPos);
					dropDrops(pLevel, drops, secondPos);
				}
			}
		}
	}
}
