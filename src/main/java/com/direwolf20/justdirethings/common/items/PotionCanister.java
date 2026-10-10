package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.containers.PotionCanisterContainer;
import com.direwolf20.justdirethings.common.containers.handlers.PotionCanisterHandler;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.util.MagicHelpers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;

public class PotionCanister extends Item {
	public static final String TOOL_CONTENTS = "tool_contents";
	private static final String POTION_CONTENTS = "potion_contents";
	private static final String POTION_AMOUNT = "potion_amount";
	private static final int BASE_POTION_COLOR = 0xFF385DC6;

	public PotionCanister() {
		super(new Properties()
				.tab(ModSetup.TAB_JUSTDIRETHINGS)
				.stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);

		NetworkHooks.openGui((ServerPlayer) player, new SimpleMenuProvider(
				(windowId, playerInventory, playerEntity) -> new PotionCanisterContainer(windowId, playerInventory, player, itemstack), new TranslatableComponent("")), (buf -> {
			buf.writeItem(itemstack);
		}));

		return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null) {
			return;
		}

		ItemStack potionContents = PotionCanister.getPotionContents(stack);
		int potionAmt = PotionCanister.getPotionAmount(stack);
		if (potionAmt == 0 || potionContents.isEmpty()) return;

		tooltip.add(new TextComponent(MagicHelpers.formatted(potionAmt) + "/" + MagicHelpers.formatted(PotionCanister.getMaxMB())));
		PotionUtils.addPotionTooltip(potionContents, tooltip, 1.0F);
	}

	public static int getMaxMB() {
		return 1000;
	}

	// A potion stack, so PotionUtils can read it
	public static ItemStack getPotionContents(ItemStack itemStack) {
		CompoundTag tag = itemStack.getTagElement(POTION_CONTENTS);
		if (tag == null) return ItemStack.EMPTY;
		ItemStack potionContents = new ItemStack(Items.POTION);
		potionContents.setTag(tag.copy());
		return potionContents;
	}

	public static void setPotionContents(ItemStack itemStack, ItemStack potionContents) {
		CompoundTag tag = potionContents.getTag();
		if (tag == null)
			itemStack.removeTagKey(POTION_CONTENTS);
		else
			itemStack.addTagElement(POTION_CONTENTS, tag.copy());
	}

	public static ItemStack readPotionContents(ItemStack potion) {
		ItemStack potionContents = PotionUtils.setCustomEffects(PotionUtils.setPotion(new ItemStack(Items.POTION), PotionUtils.getPotion(potion)), PotionUtils.getCustomEffects(potion));
		getCustomColor(potion).ifPresent(color -> potionContents.getOrCreateTag().putInt(PotionUtils.TAG_CUSTOM_POTION_COLOR, color));
		return potionContents.hasTag() ? potionContents : ItemStack.EMPTY;
	}

	private static OptionalInt getCustomColor(ItemStack potionContents) {
		CompoundTag tag = potionContents.getTag();
		if (tag != null && tag.contains(PotionUtils.TAG_CUSTOM_POTION_COLOR, Tag.TAG_ANY_NUMERIC))
			return OptionalInt.of(tag.getInt(PotionUtils.TAG_CUSTOM_POTION_COLOR));
		return OptionalInt.empty();
	}

	// MobEffectInstance.equals ignores particles and icon
	private static boolean samePotionContents(ItemStack first, ItemStack second) {
		if (PotionUtils.getPotion(first) != PotionUtils.getPotion(second) || !getCustomColor(first).equals(getCustomColor(second)))
			return false;
		List<MobEffectInstance> firstEffects = PotionUtils.getCustomEffects(first);
		List<MobEffectInstance> secondEffects = PotionUtils.getCustomEffects(second);
		if (firstEffects.size() != secondEffects.size())
			return false;
		for (int i = 0; i < firstEffects.size(); i++) {
			MobEffectInstance firstEffect = firstEffects.get(i);
			MobEffectInstance secondEffect = secondEffects.get(i);
			if (firstEffect.getDuration() != secondEffect.getDuration() || firstEffect.getAmplifier() != secondEffect.getAmplifier()
					|| firstEffect.isAmbient() != secondEffect.isAmbient() || firstEffect.isVisible() != secondEffect.isVisible()
					|| firstEffect.showIcon() != secondEffect.showIcon() || firstEffect.getEffect() != secondEffect.getEffect())
				return false;
		}
		return true;
	}

	public static void attemptFill(ItemStack canister) {
		if (!(canister.getItem() instanceof PotionCanister)) return;
		PotionCanisterHandler handler = new PotionCanisterHandler(canister, TOOL_CONTENTS, 1);
		ItemStack potion = handler.getStackInSlot(0);
		if (potion.isEmpty() || !(potion.getItem() instanceof PotionItem)) return;
		ItemStack currentContents = getPotionContents(canister);
		ItemStack newContents = readPotionContents(potion);
		if (currentContents.isEmpty() || samePotionContents(currentContents, newContents)) {
			int currentAmt = getPotionAmount(canister);
			if (currentAmt + 250 <= getMaxMB()) {
				setPotionContents(canister, newContents);
				addPotionAmount(canister, 250);
				handler.setStackInSlot(0, new ItemStack(Items.GLASS_BOTTLE));
			}
		}
	}

	public static int getPotionAmount(ItemStack itemStack) {
		CompoundTag tag = itemStack.getTag();
		return tag == null ? 0 : tag.getInt(POTION_AMOUNT);
	}

	public static void addPotionAmount(ItemStack itemStack, int amt) {
		setPotionAmount(itemStack, getPotionAmount(itemStack) + amt);
	}

	public static void setPotionAmount(ItemStack itemStack, int amt) {
		itemStack.getOrCreateTag().putInt(POTION_AMOUNT, Math.max(0, Math.min(getMaxMB(), amt)));
		if (getPotionAmount(itemStack) == 0)
			setPotionContents(itemStack, ItemStack.EMPTY);
	}

	public static int getFullness(ItemStack itemStack) {
		int potionAmt = getPotionAmount(itemStack);
		if (potionAmt == 0) return 0;
		if (potionAmt > 0 && potionAmt <= 250) return 1;
		if (potionAmt > 250 && potionAmt <= 500) return 2;
		if (potionAmt > 500 && potionAmt <= 750) return 3;
		return 4;
	}

	public static int getPotionColor(ItemStack itemStack) {
		return getPotionContentsColor(getPotionContents(itemStack));
	}

	// Like PotionUtils.getColor, but opaque
	public static int getPotionContentsColor(ItemStack potionContents) {
		OptionalInt customColor = getCustomColor(potionContents);
		if (customColor.isPresent())
			return customColor.getAsInt();
		int red = 0;
		int green = 0;
		int blue = 0;
		int weight = 0;
		for (MobEffectInstance effect : PotionUtils.getMobEffects(potionContents)) {
			if (effect.isVisible()) {
				int color = effect.getEffect().getColor();
				int amplifier = effect.getAmplifier() + 1;
				red += amplifier * (color >> 16 & 255);
				green += amplifier * (color >> 8 & 255);
				blue += amplifier * (color & 255);
				weight += amplifier;
			}
		}
		return weight == 0 ? BASE_POTION_COLOR : FastColor.ARGB32.color(255, red / weight, green / weight, blue / weight);
	}
}
