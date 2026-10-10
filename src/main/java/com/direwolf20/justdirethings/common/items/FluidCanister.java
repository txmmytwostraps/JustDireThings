package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.fluids.unstableportalfluid.UnstablePortalFluidAttributes;
import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.util.MagicHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FluidCanister extends Item implements FluidContainingItem {
	private static final String FILL_MODE_KEY = "FillMode";

	public enum FillMode {
		NONE("none"),
		JDTONLY("jdtonly"),
		ALL("all");

		private final String baseName;

		FillMode(String baseName) {
			this.baseName = baseName;
		}

		public Component getTooltip() {
			return new TranslatableComponent(JustDireThings.MODID + ".fillmode." + baseName);
		}

		public FillMode next() {
			FillMode[] values = values();
			int nextOrdinal = (this.ordinal() + 1) % values.length;
			return values[nextOrdinal];
		}
	}

	public FluidCanister() {
		super(new Properties()
				.tab(ModSetup.TAB_JUSTDIRETHINGS)
				.stacksTo(1));
	}

	@Override
	public int getMaxMB() {
		return 8000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (blockhitresult.getType() == HitResult.Type.BLOCK) {
			if (player.isShiftKeyDown()) {
				if (placeFluid(level, player, itemStack, blockhitresult))
					return InteractionResultHolder.success(itemStack);
			} else {
				if (pickupFluid(level, player, itemStack, blockhitresult))
					return InteractionResultHolder.success(itemStack);
				else {
					if (placeFluid(level, player, itemStack, blockhitresult))
						return InteractionResultHolder.success(itemStack);
				}
			}
		} else {
			if (player.isShiftKeyDown()) {
				nextFillMode(itemStack);
				player.displayClientMessage(new TranslatableComponent("justdirethings.fillmode.changed", getFillMode(itemStack).getTooltip()), true);
			}
		}
		return InteractionResultHolder.fail(itemStack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide) return;
		FillMode fillMode = getFillMode(itemStack);
		if (fillMode == FillMode.NONE) return;
		if (entity instanceof Player player) {
			IFluidHandlerItem fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
			if (fluidHandler == null || fluidHandler.getFluidInTank(0).isEmpty()) return;
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack slotStack = player.getInventory().getItem(i);
				if (slotStack.getItem() instanceof FluidCanister) continue;
				if (fillMode == FillMode.JDTONLY && !JustDireThings.MODID.equals(slotStack.getItem().getCreatorModId(slotStack)))
					continue;
				IFluidHandlerItem slotFluidHandler = slotStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
				if (slotFluidHandler != null) {
					FluidStack fluidStack = fluidHandler.getFluidInTank(0);
					int amtToFill = Math.min(fluidStack.getAmount(), 100);
					int acceptedFluid = slotFluidHandler.fill(new FluidStack(fluidStack.getFluid(), amtToFill), IFluidHandler.FluidAction.SIMULATE);
					if (acceptedFluid > 0) {
						FluidStack extractedFluid = fluidHandler.drain(new FluidStack(fluidStack.getFluid(), acceptedFluid), IFluidHandler.FluidAction.EXECUTE);
						slotFluidHandler.fill(extractedFluid, IFluidHandler.FluidAction.EXECUTE);
					}
				}
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null) {
			return;
		}

		IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		if (fluidHandler == null) {
			return;
		}
		FluidStack fluidStack = fluidHandler.getFluidInTank(0);
		int fluidColor = getFluidColor(stack);

		Style fluidStyle = Style.EMPTY.withColor(TextColor.fromRgb(fluidColor));

		tooltip.add(new TranslatableComponent("justdirethings.fluidname").withStyle(ChatFormatting.GRAY).append(new TranslatableComponent(fluidStack.getDisplayName().getString()).withStyle(fluidStyle)));
		tooltip.add(new TranslatableComponent("justdirethings.fluidamt").withStyle(ChatFormatting.GRAY).append(new TextComponent(MagicHelpers.formatted(fluidStack.getAmount()) + "/" + MagicHelpers.formatted(getMaxMB())).withStyle(ChatFormatting.GREEN)));
		tooltip.add(new TranslatableComponent("justdirethings.fillmode").withStyle(ChatFormatting.GRAY).append(new TranslatableComponent(getFillMode(stack).getTooltip().getString()).withStyle(ChatFormatting.GREEN)));
	}

	public boolean placeFluid(Level level, Player player, ItemStack itemStack, BlockHitResult blockhitresult) {
		BlockPos blockpos = blockhitresult.getBlockPos();
		BlockState blockstate = level.getBlockState(blockpos);
		if (blockstate.getBlock() instanceof LiquidBlock && !player.isShiftKeyDown()) return false;
		IFluidHandlerItem fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		if (fluidHandler == null) return false;
		FluidStack fluidStack = fluidHandler.getFluidInTank(0);
		if (fluidStack.isEmpty() || fluidStack.getAmount() < 1000) return false;
		Direction direction = blockhitresult.getDirection();
		BlockPos blockpos1 = blockpos.relative(direction);
		BlockPos blockpos2 = canBlockContainFluid(player, level, blockpos, blockstate, fluidStack.getFluid()) ? blockpos : blockpos1;
		if (this.emptyContents(player, level, blockpos2, itemStack)) {
			fluidHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
			return true;
		}

		return false;
	}

	public boolean pickupFluid(Level level, Player player, ItemStack itemStack, BlockHitResult blockhitresult) {
		BlockPos blockpos = blockhitresult.getBlockPos();
		BlockState blockstate1 = level.getBlockState(blockpos);
		if (blockstate1.getBlock() instanceof LiquidBlock liquidBlock) {
			IFluidHandlerItem fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
			if (fluidHandler == null) return false;
			Fluid fluid = liquidBlock.getFluid();
			FluidStack fluidStack = fluidHandler.getFluidInTank(0);
			if (!liquidBlock.getFluid().isSource(liquidBlock.getFluid().getSource(false)) || (!fluidStack.isEmpty() && fluidStack.getFluid() != fluid))
				return false;

			int filledAmt = fluidHandler.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.SIMULATE);
			if (filledAmt == 1000) {
				ItemStack itemstack2 = liquidBlock.pickupBlock(level, blockpos, blockstate1);
				fluidHandler.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
				liquidBlock.getPickupSound(blockstate1).ifPresent(p_150709_ -> player.playSound(p_150709_, 1.0F, 1.0F));
				if (!level.isClientSide) {
					CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemstack2);
				}
			}
			return true;
		}
		return false;
	}

	public boolean emptyContents(@Nullable Player player, Level level, BlockPos pos, @Nullable ItemStack container) {
		FluidStack fluidData = getFluidData(container);
		if (fluidData.isEmpty()) return false;

		Fluid fluid = fluidData.getFluid();
		if (!(fluid instanceof FlowingFluid flowingFluid)) return false;

		BlockState blockState = level.getBlockState(pos);
		Block block = blockState.getBlock();
		boolean canBeReplaced = blockState.canBeReplaced(fluid);

		if (!blockState.isAir() && !canBeReplaced) {
			if (block instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(level, pos, blockState, fluid)) {
				liquidBlockContainer.placeLiquid(level, pos, blockState, flowingFluid.getSource(false));
				playEmptySound(player, level, pos, fluid);
				return true;
			}
			return false;
		}
		FluidStack fluidStack = new FluidStack(fluid, 1000);
		if (UnstablePortalFluidAttributes.vaporizesOnPlacement(level, pos, fluidStack)) {
			fluid.getAttributes().vaporize(player, level, pos, fluidStack);
			return true;
		}

		if (!level.isClientSide && canBeReplaced && !blockState.getMaterial().isLiquid()) {
			level.destroyBlock(pos, true);
		}

		if (level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), 11) || blockState.getFluidState().isSource()) {
			playEmptySound(player, level, pos, fluid);
			return true;
		}
		return false;
	}

	protected void playEmptySound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, Fluid fluid) {
		SoundEvent soundevent = fluid.getAttributes().getEmptySound(pLevel, pPos);
		if (soundevent == null)
			soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
		pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);
	}

	protected boolean canBlockContainFluid(@Nullable Player player, Level worldIn, BlockPos posIn, BlockState blockstate, Fluid fluid) {
		return blockstate.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer) blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, fluid);
	}

	public static FluidStack getFluidData(ItemStack itemStack) {
		CompoundTag fluidTag = itemStack.getTagElement(FluidHandlerItemStack.FLUID_NBT_KEY);
		return fluidTag == null ? FluidStack.EMPTY : FluidStack.loadFluidStackFromNBT(fluidTag);
	}

	public static int getFullness(ItemStack itemStack) {
		FluidStack fluidData = getFluidData(itemStack);
		if (!fluidData.isEmpty()) {
			int mb = fluidData.getAmount();
			return (int) Math.ceil((double) mb / 1000);
		}
		return 0;
	}

	public static int getFluidColor(ItemStack itemStack) {
		FluidStack fluidData = getFluidData(itemStack);
		if (!fluidData.isEmpty()) {
			if (fluidData.getFluid().isSame(Fluids.LAVA)) //Special case lava
				return 0xFFFF4500;
			return fluidData.getFluid().getAttributes().getColor(fluidData);
		}
		return 0xFFFFFFFF;
	}

	public static FillMode getFillMode(ItemStack itemStack) {
		CompoundTag tag = itemStack.getTag();
		return FillMode.values()[tag == null ? 0 : tag.getInt(FILL_MODE_KEY)];
	}

	public static void nextFillMode(ItemStack itemStack) {
		itemStack.getOrCreateTag().putInt(FILL_MODE_KEY, getFillMode(itemStack).next().ordinal());
	}
}
