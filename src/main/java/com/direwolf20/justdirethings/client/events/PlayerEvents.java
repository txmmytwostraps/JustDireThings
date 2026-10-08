package com.direwolf20.justdirethings.client.events;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.items.PortalGun;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.LeftClickableTool;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseHoe;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.LeftClickPayload;
import com.direwolf20.justdirethings.common.network.data.PortalGunLeftClickPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = JustDireThings.MODID)
public class PlayerEvents {
	private static BlockPos destroyPos = BlockPos.ZERO;
	private static int gameTicksMining = 0;
	// LeftClickBlock doesn't say which break step fired it. The game mode mixins set these.
	public static ServerPlayer serverBlockActionPlayer = null;
	public static ServerboundPlayerActionPacket.Action serverBlockAction = ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK;
	public static boolean clientMineHold = false;

	@SubscribeEvent
	public static void LeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		ItemStack itemStack = event.getItemStack();
		if (itemStack.getItem() instanceof ToggleableTool toggleableTool && itemStack.getItem() instanceof LeftClickableTool) {
			activateAbilities(itemStack, toggleableTool, event.getPlayer(), event.getHand(), true, BlockPos.ZERO, Direction.DOWN);
		}
		if (itemStack.getItem() instanceof PortalGun)
			PacketHandler.CHANNEL.sendToServer(new PortalGunLeftClickPayload());
	}

	@SubscribeEvent
	public static void LeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		ItemStack itemStack = event.getItemStack();
		boolean breakStart = isBreakStart(event);
		if (itemStack.getItem() instanceof ToggleableTool toggleableTool && itemStack.getItem() instanceof LeftClickableTool && event.getFace() != null) {
			if (breakStart) { //Only start has the 'proper' direction - this also runs both client AND server side!
				activateAbilities(itemStack, toggleableTool, event.getPlayer(), event.getHand(), false, event.getPos(), event.getFace());
			}
		}
		if (itemStack.getItem() instanceof ToggleableTool toggleableTool && !(itemStack.getItem() instanceof BaseHoe) && (toggleableTool.hasAbility(Ability.HAMMER) || toggleableTool.hasAbility(Ability.OREMINER)) && event.getFace() != null) {
			doExtraCrumblings(event, itemStack, toggleableTool, breakStart);
		}
		if (event.getPlayer().level.isClientSide() && itemStack.getItem() instanceof PortalGun && breakStart)
			PacketHandler.CHANNEL.sendToServer(new PortalGunLeftClickPayload());
	}

	private static boolean isBreakStart(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getPlayer().level.isClientSide())
			return !clientMineHold;
		return event.getPlayer() != serverBlockActionPlayer || serverBlockAction == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK;
	}

	private static void doExtraCrumblings(PlayerInteractEvent.LeftClickBlock event, ItemStack itemStack, ToggleableTool toggleableTool, boolean breakStart) {
		Player player = event.getPlayer();
		Level level = player.level;
		BlockPos blockPos = event.getPos();
		BlockState blockState = level.getBlockState(blockPos);
		if (breakStart) { //Client and Server
			if (level.isClientSide) {
				gameTicksMining = 0;
				destroyPos = blockPos;
			}
		}
		if (!breakStart && !level.isClientSide) { //Server Only
			cancelBreaks(level, blockState, blockPos, player, toggleableTool, itemStack);
		}
		if (!breakStart && level.isClientSide) { //Client Only
			if (blockPos.equals(destroyPos)) {
				gameTicksMining++;
			} else {
				gameTicksMining = 0;
				destroyPos = blockPos;
			}
			incrementDestroyProgress(level, blockState, blockPos, player, toggleableTool, itemStack);
		}
	}

	private static float incrementDestroyProgress(Level level, BlockState pState, BlockPos pPos, Player player, ToggleableTool toggleableTool, ItemStack toggleableToolStack) {
		Set<BlockPos> breakBlockPositions = toggleableTool.getBreakBlockPositions(toggleableToolStack, level, pPos, player, pState);
		int i = gameTicksMining;
		float f = pState.getDestroyProgress(player, player.level, pPos) * (float) (i + 1);
		int j = (int) (f * 10.0F);
		for (BlockPos blockPos : breakBlockPositions) {
			if (blockPos.equals(pPos)) continue; //Let the vanilla mechanics handle the block we're hitting
			if (level.isClientSide)
				level.destroyBlockProgress(player.getId() + generatePosHash(blockPos), blockPos, j);
			else
				sendDestroyBlockProgress(player.getId() + generatePosHash(blockPos), blockPos, -1, (ServerPlayer) player);
		}
		return f;
	}

	private static void cancelBreaks(Level level, BlockState pState, BlockPos pPos, Player player, ToggleableTool toggleableTool, ItemStack toggleableToolStack) {
		Set<BlockPos> breakBlockPositions = toggleableTool.getBreakBlockPositions(toggleableToolStack, level, pPos, player, pState);
		for (BlockPos blockPos : breakBlockPositions) {
			if (blockPos.equals(pPos)) continue; //Let the vanilla mechanics handle the block we're hitting
			player.level.destroyBlockProgress(player.getId() + generatePosHash(blockPos), blockPos, -1);
		}
	}

	public static int generatePosHash(BlockPos blockPos) {
		return (31 * 31 * blockPos.getX()) + (31 * blockPos.getY()) + blockPos.getZ(); //For now this is probably good enough, will add more randomness if needed
	}

	public static void sendDestroyBlockProgress(int pBreakerId, BlockPos pPos, int pProgress, ServerPlayer serverPlayer) {
		serverPlayer.connection.send(new ClientboundBlockDestructionPacket(pBreakerId, pPos, pProgress));
	}

	private static void activateAbilities(ItemStack itemStack, ToggleableTool toggleableTool, Player player, InteractionHand hand, boolean air, BlockPos blockPos, Direction direction) {
		Set<Ability> abilities = LeftClickableTool.getLeftClickList(itemStack);
		if (!abilities.isEmpty()) {
			//Do them client side and Server side, since some abilities (like ore scanner) are client side activated.
			if (air) { //Air
				toggleableTool.useAbility(player.level, player, hand, false);
				PacketHandler.CHANNEL.sendToServer(new LeftClickPayload(0, hand == InteractionHand.MAIN_HAND, BlockPos.ZERO, -1, -1, -1, false)); //Type 0 == air
			} else { //Block - No need for a packet since this will run both client and server side! (See above!)
				UseOnContext useoncontext = new UseOnContext(player.level, player, hand, itemStack, new BlockHitResult(Vec3.atCenterOf(blockPos), direction, blockPos, false));
				toggleableTool.useOnAbility(useoncontext, false);
			}

		}
	}
}
