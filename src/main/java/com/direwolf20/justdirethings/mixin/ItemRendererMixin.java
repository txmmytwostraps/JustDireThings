package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.client.itemcustomrenders.FluidbarDecorator;
import com.direwolf20.justdirethings.common.items.FluidCanister;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Draws the fluid bar after the vanilla decorations. The canister is drawn here too, since vanilla drops the tint's alpha.
 */
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Shadow
	@Final
	private ItemColors itemColors;

	@Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
	private void onRenderGuiItemDecorations(Font font, ItemStack stack, int x, int y, @Nullable String text, CallbackInfo ci) {
		FluidbarDecorator.render(stack, x, y);
	}

	@Inject(method = "renderQuadList", at = @At("HEAD"), cancellable = true)
	private void onRenderQuadList(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, ItemStack stack, int combinedLight, int combinedOverlay, CallbackInfo ci) {
		if (!(stack.getItem() instanceof FluidCanister))
			return;
		PoseStack.Pose pose = poseStack.last();
		for (BakedQuad quad : quads) {
			int color = quad.isTinted() ? itemColors.getColor(stack, quad.getTintIndex()) : -1;
			float alpha = (float) (color >> 24 & 255) / 255.0F;
			float red = (float) (color >> 16 & 255) / 255.0F;
			float green = (float) (color >> 8 & 255) / 255.0F;
			float blue = (float) (color & 255) / 255.0F;
			buffer.putBulkData(pose, quad, red, green, blue, alpha, combinedLight, combinedOverlay, true);
		}
		ci.cancel();
	}
}
