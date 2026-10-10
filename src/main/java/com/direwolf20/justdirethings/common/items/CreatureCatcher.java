package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.client.renderers.JustDireItemRenderer;
import com.direwolf20.justdirethings.common.entities.CreatureCatcherEntity;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.util.MiscTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class CreatureCatcher extends Item {
	public static final String ENTITY_TYPE = "entityType";

	public CreatureCatcher() {
		super(new Properties().tab(ModSetup.TAB_JUSTDIRETHINGS));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack itemStack = pPlayer.getItemInHand(pHand);
		if (MiscTools.isRestrictedDimension(pLevel.dimension(), Config.RESTRICT_CREATURE_CATCHER))
			return InteractionResultHolder.fail(itemStack);
		pLevel.playSound(
				null,
				pPlayer.getX(),
				pPlayer.getY(),
				pPlayer.getZ(),
				SoundEvents.SNOWBALL_THROW,
				SoundSource.NEUTRAL,
				0.5F,
				0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
		);
		if (!pLevel.isClientSide) {
			CreatureCatcherEntity creatureCatcherEntity = new CreatureCatcherEntity(pLevel, pPlayer);
			creatureCatcherEntity.setItem(itemStack);
			creatureCatcherEntity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
			pLevel.addFreshEntity(creatureCatcherEntity);
		}

		if (!pPlayer.getAbilities().instabuild) {
			itemStack.shrink(1);
		}

		return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
	}

	public static boolean hasEntity(ItemStack itemStack) {
		CompoundTag tag = itemStack.getTag();
		return tag != null && tag.contains(ENTITY_TYPE);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null) {
			return;
		}

		Mob mob = CreatureCatcherEntity.getEntityFromItemStack(stack, level);
		if (mob == null) return;

		tooltip.add(new TranslatableComponent("justdirethings.creature")
				.withStyle(ChatFormatting.DARK_GRAY)
				.append(new TextComponent("")
						.append(mob.getName())
						.withStyle(ChatFormatting.GREEN)));
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			final JustDireItemRenderer diremodel = new JustDireItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return diremodel;
			}
		});
	}
}
