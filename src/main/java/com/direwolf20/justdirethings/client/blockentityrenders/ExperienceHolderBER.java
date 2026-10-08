package com.direwolf20.justdirethings.client.blockentityrenders;

import com.direwolf20.justdirethings.client.blockentityrenders.baseber.AreaAffectingBER;
import com.direwolf20.justdirethings.common.blockentities.ExperienceHolderBE;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ExperienceHolderBER extends AreaAffectingBER {
	public static final ItemStack itemStack = new ItemStack(Items.EXPERIENCE_BOTTLE);

	public ExperienceHolderBER(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(BlockEntity blockentity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightsIn, int combinedOverlayIn) {
		super.render(blockentity, partialTicks, matrixStackIn, bufferIn, combinedLightsIn, combinedOverlayIn);
		if (blockentity instanceof ExperienceHolderBE experienceHolderBE)
			this.renderItemStack(experienceHolderBE, matrixStackIn, bufferIn, combinedOverlayIn);
	}

	private void renderItemStack(ExperienceHolderBE blockEntity, PoseStack poseStack, MultiBufferSource bufferIn, int combinedOverlayIn) {
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		Direction direction = blockEntity.getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
		long millis = System.currentTimeMillis();

		poseStack.pushPose();
		poseStack.translate(0.5f + (direction.getStepX() * 0.3f), 0.5f + (direction.getStepY() * 0.3f), 0.5f + (direction.getStepZ() * 0.3f));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(direction.getStepZ() * -90));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(direction.getStepX() * 90));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(direction.getStepY() == 1 ? 0 : 180));
		float angle = ((millis / 15) % 360);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(angle));
		poseStack.scale(.15f, .15f, .15f);
		itemRenderer.renderStatic(null, itemStack, ItemTransforms.TransformType.FIXED, false, poseStack, bufferIn, Minecraft.getInstance().level, LightTexture.FULL_BRIGHT, combinedOverlayIn, 0);
		poseStack.popPose();
	}
}
