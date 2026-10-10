package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.client.screens.ModScreens;
import com.direwolf20.justdirethings.common.blockentities.basebe.AreaAffectingBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.FilterableBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.RedstoneControlledBE;
import com.direwolf20.justdirethings.common.containers.handlers.FilterBasicHandler;
import com.direwolf20.justdirethings.setup.ModSetup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MachineSettingsCopier extends Item {
	private static final String COPIED_DATA_KEY = "CopiedData";
	private static final String COPY_AREA_KEY = "CopyArea";
	private static final String COPY_OFFSET_KEY = "CopyOffset";
	private static final String COPY_FILTER_KEY = "CopyFilter";
	private static final String COPY_REDSTONE_KEY = "CopyRedstone";

	public MachineSettingsCopier() {
		super(new Properties()
				.tab(ModSetup.TAB_JUSTDIRETHINGS)
				.stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (!level.isClientSide() || !player.isShiftKeyDown())
			return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);

		if (level.isClientSide)
			ModScreens.openMachineSettingsCopierScreen(itemstack);

		return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		Level level = pContext.getLevel();
		if (level.isClientSide) return InteractionResult.SUCCESS;
		BlockEntity blockEntity = level.getBlockEntity(pContext.getClickedPos());

		if (!(blockEntity instanceof BaseMachineBE)) return InteractionResult.PASS;
		ItemStack itemStack = pContext.getItemInHand();

		Player player = pContext.getPlayer();

		if (player.isShiftKeyDown()) { //Copy
			saveSettings(level, blockEntity, itemStack);
			player.displayClientMessage(new TranslatableComponent("justdirethings.settingscopied"), true);
			player.playNotifySound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.PLAYERS, 1.0F, 1.0F);
		} else { //Paste
			loadSettings(level, blockEntity, itemStack);
			player.displayClientMessage(new TranslatableComponent("justdirethings.settingspasted"), true);
			player.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
		}

		return InteractionResult.SUCCESS;
	}

	public void loadSettings(Level level, BlockEntity blockEntity, ItemStack itemStack) {
		CompoundTag copiedData = itemStack.getTagElement(COPIED_DATA_KEY);
		if (copiedData == null) return;
		CompoundTag compoundTag = copiedData.copy();
		if (compoundTag.isEmpty()) return;

		if (blockEntity instanceof AreaAffectingBE areaAffectingBE) {
			if (getCopyArea(itemStack))
				areaAffectingBE.loadAreaOnly(compoundTag);
			if (getCopyOffset(itemStack))
				areaAffectingBE.loadOffsetOnly(compoundTag);
		}

		if (getCopyFilter(itemStack) && blockEntity instanceof FilterableBE filterableBE) {
			filterableBE.loadFilterSettings(compoundTag);
			if (compoundTag.contains("filteredItems")) {
				CompoundTag filteredItems = compoundTag.getCompound("filteredItems");
				FilterBasicHandler filterBasicHandler = filterableBE.getFilterHandler();
				filterBasicHandler.deserializeNBT(filteredItems);
			}
		}

		if (getCopyRedstone(itemStack) && blockEntity instanceof RedstoneControlledBE redstoneControlledBE)
			redstoneControlledBE.loadRedstoneSettings(compoundTag);

		((BaseMachineBE) blockEntity).markDirtyClient();
	}

	public void saveSettings(Level level, BlockEntity blockEntity, ItemStack itemStack) {
		CompoundTag compoundTag = new CompoundTag();
		if (blockEntity instanceof AreaAffectingBE areaAffectingBE) {
			if (getCopyArea(itemStack))
				areaAffectingBE.saveAreaOnly(compoundTag);
			if (getCopyOffset(itemStack))
				areaAffectingBE.saveOffsetOnly(compoundTag);
		}

		if (getCopyFilter(itemStack) && blockEntity instanceof FilterableBE filterableBE) {
			filterableBE.saveFilterSettings(compoundTag);
			FilterBasicHandler filterBasicHandler = filterableBE.getFilterHandler();
			compoundTag.put("filteredItems", filterBasicHandler.serializeNBT());
		}

		if (getCopyRedstone(itemStack) && blockEntity instanceof RedstoneControlledBE redstoneControlledBE)
			redstoneControlledBE.saveRedstoneSettings(compoundTag);

		if (!compoundTag.isEmpty())
			itemStack.addTagElement(COPIED_DATA_KEY, compoundTag);
	}

	public static void setSettings(ItemStack itemStack, boolean area, boolean offset, boolean filter, boolean redstone) {
		CompoundTag tag = itemStack.getOrCreateTag();
		tag.putBoolean(COPY_AREA_KEY, area);
		tag.putBoolean(COPY_OFFSET_KEY, offset);
		tag.putBoolean(COPY_FILTER_KEY, filter);
		tag.putBoolean(COPY_REDSTONE_KEY, redstone);
	}

	public static boolean getCopyArea(ItemStack itemStack) {
		return getCopySetting(itemStack, COPY_AREA_KEY);
	}

	public static boolean getCopyOffset(ItemStack itemStack) {
		return getCopySetting(itemStack, COPY_OFFSET_KEY);
	}

	public static boolean getCopyFilter(ItemStack itemStack) {
		return getCopySetting(itemStack, COPY_FILTER_KEY);
	}

	public static boolean getCopyRedstone(ItemStack itemStack) {
		return getCopySetting(itemStack, COPY_REDSTONE_KEY);
	}

	private static boolean getCopySetting(ItemStack itemStack, String key) {
		CompoundTag tag = itemStack.getTag();
		return tag == null || !tag.contains(key) || tag.getBoolean(key);
	}
}
