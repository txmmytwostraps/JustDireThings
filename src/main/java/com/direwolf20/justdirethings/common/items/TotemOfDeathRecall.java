package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class TotemOfDeathRecall extends Item {
	private static final String BOUND_TO = "boundTo";

	public TotemOfDeathRecall() {
		super(new Properties()
				.tab(ModSetup.TAB_JUSTDIRETHINGS)
				.stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (getBoundTo(itemStack) == null) return new InteractionResultHolder<>(InteractionResult.PASS, itemStack);
		player.startUsingItem(hand);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.BOW;
	}

	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof Player player) {
			int usedDuration = this.getUseDuration(stack) - timeLeft;
			if (usedDuration >= 20) {  // 60 ticks = 3 seconds
				// Retrieve death location from NBT and teleport
				if (!world.isClientSide) {
					if (stack.getTagElement(BOUND_TO) != null) {
						NBTHelpers.GlobalVec3 globalPos = getBoundTo(stack);
						if (globalPos == null) return;
						Vec3 position = globalPos.position();
						ServerLevel targetLevel = world.getServer().getLevel(globalPos.dimension());
						if (targetLevel != null) {
							MiscHelpers.teleportEntity(player, targetLevel, position.x(), position.y(), position.z(), player.getYRot(), player.getXRot());
							stack.shrink(1);
							world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
						}
					}
				}
			}
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;  // Max duration (arbitrary large number)
	}

	@Override
	public boolean isFoil(ItemStack pStack) {
		return getBoundTo(pStack) != null;
	}

	public static NBTHelpers.GlobalVec3 getBoundTo(ItemStack stack) {
		CompoundTag tag = stack.getTagElement(BOUND_TO);
		if (tag != null)
			return NBTHelpers.nbtToGlobalVec3(tag);
		return null;
	}

	public static void setBoundTo(ItemStack stack, NBTHelpers.GlobalVec3 globalPos) {
		stack.addTagElement(BOUND_TO, NBTHelpers.globalVec3ToNBT(globalPos));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null) {
			return;
		}
		NBTHelpers.GlobalVec3 boundPos = getBoundTo(stack);
		ChatFormatting chatFormatting = ChatFormatting.DARK_PURPLE;
		if (boundPos != null) {
			tooltip.add(new TranslatableComponent("justdirethings.boundto", I18n.get(boundPos.dimension().location().getPath()), boundPos.toVec3ShortString()).withStyle(chatFormatting));
		}
	}
}
