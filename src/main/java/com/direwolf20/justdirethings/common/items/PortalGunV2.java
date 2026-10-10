package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.entities.PortalProjectile;
import com.direwolf20.justdirethings.common.items.interfaces.BasePoweredItem;
import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.MiscTools;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PortalGunV2 extends BasePoweredItem implements PoweredItem, FluidContainingItem {
	public static final int MAX_FAVORITES = 12;
	public static final int maxMB = 8000;
	private static final String PORTALGUN_FAVORITE = "portalgun_favorite";
	private static final String PORTAL_GUN_FAVORITES = "portal_gun_favorites";
	private static final String PORTAL_GUN_PREVIOUS = "portal_gun_previous";
	private static final String PORTAL_GUN_STAY_OPEN = "portal_gun_stay_open";

	public PortalGunV2() {
		super(new Properties()
				.tab(ModSetup.TAB_JUSTDIRETHINGS)
				.stacksTo(1));
	}

	@Override
	public int getMaxEnergy() {
		return Config.PORTAL_GUN_V2_RF_CAPACITY.get();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (blockhitresult.getType() == HitResult.Type.BLOCK) {
			if (pickupFluid(level, player, itemStack, blockhitresult))
				return InteractionResultHolder.fail(itemStack);
		}
		if (!level.isClientSide) {
			spawnProjectile(level, player, itemStack, true);
		}
		return InteractionResultHolder.fail(itemStack);
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
		tooltip.add(new TranslatableComponent("justdirethings.portalfluidamt", MagicHelpers.formatted(fluidHandler.getFluidInTank(0).getAmount()), MagicHelpers.formatted(maxMB)).withStyle(ChatFormatting.GREEN));
	}

	public static boolean pickupFluid(Level level, Player player, ItemStack itemStack, BlockHitResult blockhitresult) {
		BlockPos blockpos = blockhitresult.getBlockPos();
		BlockState blockstate1 = level.getBlockState(blockpos);
		if (blockstate1.is(Registration.PORTAL_FLUID_BLOCK.get())) {
			LiquidBlock portalFluidBlock = Registration.PORTAL_FLUID_BLOCK.get();
			IFluidHandlerItem fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
			if (fluidHandler == null) return true;
			int filledAmt = fluidHandler.fill(new FluidStack(Registration.PORTAL_FLUID_SOURCE.get(), 1000), IFluidHandler.FluidAction.SIMULATE);
			if (filledAmt == 1000) {
				ItemStack itemstack2 = portalFluidBlock.pickupBlock(level, blockpos, blockstate1);
				fluidHandler.fill(new FluidStack(Registration.PORTAL_FLUID_SOURCE.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
				portalFluidBlock.getPickupSound(blockstate1).ifPresent(p_150709_ -> player.playSound(p_150709_, 1.0F, 1.0F));
				if (!level.isClientSide) {
					CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemstack2);
				}
			}
			return true;
		}
		return false;
	}

	public static void spawnProjectile(Level level, Player player, ItemStack itemStack, boolean isPrimaryType) {
		NBTHelpers.PortalDestination portalDestination = player.isShiftKeyDown() ? getPrevious(itemStack) : getSelectedFavorite(itemStack);
		if (portalDestination == null || portalDestination.equals(NBTHelpers.PortalDestination.EMPTY)) return;
		if (MiscTools.isRestrictedDimension(level.dimension(), Config.RESTRICT_PORTAL_GUNS) || MiscTools.isRestrictedDimension(portalDestination.globalVec3().dimension(), Config.RESTRICT_PORTAL_GUNS)) return;
		if (((ServerLevel) level).getServer().getLevel(portalDestination.globalVec3().dimension()) == null) return;
		int cost = calculateFluidCost((ServerLevel) level, player, portalDestination);
		if (!FluidContainingItem.hasEnoughFluid(itemStack, cost)) {
			player.displayClientMessage(new TranslatableComponent("justdirethings.lowportalfluid"), true);
			player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			return;
		}
		if (!PoweredItem.consumeEnergy(itemStack, Config.PORTAL_GUN_V2_RF_COST.get())) {
			player.displayClientMessage(new TranslatableComponent("justdirethings.lowenergy"), true);
			player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
			return;
		}
		PortalProjectile projectile = new PortalProjectile(level, player, PortalGun.getUUID(itemStack), isPrimaryType, true, portalDestination);
		projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1F, 1.0F);
		level.addFreshEntity(projectile);
		FluidContainingItem.consumeFluid(itemStack, cost);
		setPrevious(player, itemStack);
	}

	public static int calculateFluidCost(ServerLevel sourceLevel, Player player, NBTHelpers.PortalDestination portalDestination) {
		if (player.isCreative()) return 0;
		ServerLevel targetLevel = sourceLevel.getServer().getLevel(portalDestination.globalVec3().dimension());
		if (!targetLevel.equals(sourceLevel)) {
			return 100;
		}
		HitResult result = player.pick(5, 0f, false); //This will get the location the projectile will hit, or close to it probably
		Vec3 targetPosition = portalDestination.globalVec3().position();
		double distance = targetPosition.distanceTo(result.getLocation());
		return Math.min((int) Math.ceil(distance * 0.25), 100);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	public static NBTHelpers.PortalDestination getSelectedFavorite(ItemStack itemStack) {
		List<NBTHelpers.PortalDestination> favoritesList = getFavorites(itemStack);
		if (favoritesList.isEmpty()) return null;
		return favoritesList.get(getFavoritePosition(itemStack));
	}

	public static NBTHelpers.PortalDestination getFavorite(ItemStack itemStack, int slot) {
		List<NBTHelpers.PortalDestination> favoritesList = getFavorites(itemStack);
		if (favoritesList.isEmpty()) return null;
		return favoritesList.get(slot);
	}

	public static int getFavoritePosition(ItemStack itemStack) {
		CompoundTag tag = itemStack.getTag();
		return tag == null ? 0 : tag.getInt(PORTALGUN_FAVORITE);
	}

	public static void setFavoritePosition(ItemStack itemStack, int favorite) {
		itemStack.getOrCreateTag().putInt(PORTALGUN_FAVORITE, favorite);
	}

	public static List<NBTHelpers.PortalDestination> getFavorites(ItemStack itemStack) {
		List<NBTHelpers.PortalDestination> favorites = new ArrayList<>();
		CompoundTag tag = itemStack.getTag();
		if (tag == null) return favorites;
		ListTag listTag = tag.getList(PORTAL_GUN_FAVORITES, Tag.TAG_COMPOUND);
		for (int i = 0; i < listTag.size(); i++)
			favorites.add(NBTHelpers.PortalDestination.fromNBT(listTag.getCompound(i)));
		return favorites;
	}

	public static void setFavorites(ItemStack itemStack, List<NBTHelpers.PortalDestination> favorites) {
		ListTag listTag = new ListTag();
		for (NBTHelpers.PortalDestination favorite : favorites)
			listTag.add(NBTHelpers.PortalDestination.toNBT(favorite));
		itemStack.getOrCreateTag().put(PORTAL_GUN_FAVORITES, listTag);
	}

	public static void setPrevious(Player player, ItemStack itemStack) {
		Vec3 position = player.position();
		Direction facing = MiscHelpers.getFacingDirection(player);
		if (facing == Direction.DOWN) facing = Direction.NORTH; //Down is bad
		ResourceKey<Level> dimension = player.level.dimension();
		NBTHelpers.PortalDestination newDestination = new NBTHelpers.PortalDestination(new NBTHelpers.GlobalVec3(dimension, position), facing, "previous");
		itemStack.addTagElement(PORTAL_GUN_PREVIOUS, NBTHelpers.PortalDestination.toNBT(newDestination));
	}

	public static NBTHelpers.PortalDestination getPrevious(ItemStack itemStack) {
		CompoundTag tag = itemStack.getTagElement(PORTAL_GUN_PREVIOUS);
		if (tag == null) return NBTHelpers.PortalDestination.EMPTY;
		return NBTHelpers.PortalDestination.fromNBT(tag);
	}

	public static void addFavorite(ItemStack itemStack, int position, NBTHelpers.PortalDestination portalDestination) {
		if (getFavorites(itemStack).isEmpty()) {
			List<NBTHelpers.PortalDestination> list = new ArrayList<>(MAX_FAVORITES);
			for (int i = 0; i < MAX_FAVORITES; i++) {
				list.add(NBTHelpers.PortalDestination.EMPTY); //Prefill List
			}
			setFavorites(itemStack, list);
		}
		List<NBTHelpers.PortalDestination> favoritesList = getFavorites(itemStack);
		favoritesList.set(position, portalDestination);
		setFavorites(itemStack, favoritesList);
	}

	public static void removeFavorite(ItemStack itemStack, int position) {
		List<NBTHelpers.PortalDestination> favoritesList = getFavorites(itemStack);
		if (favoritesList.isEmpty()) return;
		favoritesList.set(position, NBTHelpers.PortalDestination.EMPTY);
		setFavorites(itemStack, favoritesList);
	}

	public static boolean getStayOpen(ItemStack itemStack) {
		CompoundTag tag = itemStack.getTag();
		return tag != null && tag.getBoolean(PORTAL_GUN_STAY_OPEN);
	}

	public static void setStayOpen(ItemStack itemStack, boolean stayOpen) {
		itemStack.getOrCreateTag().putBoolean(PORTAL_GUN_STAY_OPEN, stayOpen);
	}

	public static ItemStack getPortalGunv2(Player player) {
		ItemStack mainHand = player.getMainHandItem();
		if (mainHand.getItem() instanceof PortalGunV2)
			return mainHand;
		ItemStack offHand = player.getOffhandItem();
		if (offHand.getItem() instanceof PortalGunV2)
			return offHand;
		return ItemStack.EMPTY;
	}

	public static int getFullness(ItemStack itemStack) {
		IFluidHandlerItem fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		if (fluidHandler != null && !fluidHandler.getFluidInTank(0).isEmpty()) {
			float percentFull = ((float) fluidHandler.getFluidInTank(0).getAmount() / maxMB) * 100;
			if (percentFull > 0 && percentFull <= 33) {
				return 1;
			} else if (percentFull > 33 && percentFull <= 66) {
				return 2;
			} else if (percentFull > 66) {
				return 3;
			}
		}
		return 0;
	}
}
