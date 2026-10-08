package com.direwolf20.justdirethings.client.entityrenders;

import com.direwolf20.justdirethings.common.entities.CreatureCatcherEntity;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class CreatureCatcherEntityRender extends ThrownItemRenderer<CreatureCatcherEntity> {
	private final ItemRenderer itemRenderer;
	private final float scale;

	public CreatureCatcherEntityRender(EntityRendererProvider.Context pContext) {
		super(pContext);
		this.itemRenderer = pContext.getItemRenderer();
		this.scale = 1f;
	}

	@Override
	public void render(CreatureCatcherEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
		if (pEntity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(pEntity) < 12.25)) {
			pPoseStack.pushPose();
			pPoseStack.scale(this.scale, this.scale, this.scale);
			pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			float multiplier = (10f * pEntity.shrinkingTime() % 360);
			pPoseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F + multiplier));
			boolean capturing = pEntity.isCapturing();
			if (!capturing && pEntity.renderTick > 0) {
				this.itemRenderer
						.renderStatic(
								null,
								new ItemStack(Registration.CreatureCatcher.get()),
								ItemTransforms.TransformType.GROUND,
								false,
								pPoseStack,
								pBuffer,
								pEntity.level,
								pPackedLight,
								OverlayTexture.NO_OVERLAY,
								pEntity.getId()
						);
			} else {
				this.itemRenderer
						.renderStatic(
								null,
								pEntity.getItem(),
								ItemTransforms.TransformType.GROUND,
								false,
								pPoseStack,
								pBuffer,
								pEntity.level,
								pPackedLight,
								OverlayTexture.NO_OVERLAY,
								pEntity.getId()
						);
			}
			pPoseStack.popPose();
			ItemStack returnStack = pEntity.getReturnStack();
			Mob mob;
			if (!capturing)
				mob = CreatureCatcherEntity.getEntityFromItemStack(pEntity.getItem(), pEntity.level);
			else
				mob = CreatureCatcherEntity.getEntityFromItemStack(returnStack, pEntity.level);
			if (mob == null) return;
			mob.yBodyRot = pEntity.getBodyRot();
			mob.yBodyRotO = pEntity.getBodyRot();
			mob.yHeadRot = pEntity.getHeadRot();
			mob.yHeadRotO = pEntity.getHeadRot();
			pPoseStack.pushPose();
			// Calculate the interpolated position
			Vector3f entityPosition = new Vector3f((float) pEntity.getX(), (float) pEntity.getY() + (float) (pEntity.getBoundingBox().getYsize() / 2), (float) pEntity.getZ());
			Vector3f originalPosition;
			float fraction; // Shrink Factor ranging from 0.0 being full-size, to 1.0 being shrunken to nothing
			int currentShrinkingTime = Math.min(pEntity.renderTick, pEntity.getAnimationTicks());
			int lastShrinkingTime = pEntity.renderTick == 0 ? 0 : pEntity.renderTick - 1;

			float interpolatedShrinkingTime = Mth.lerp(pPartialTicks, lastShrinkingTime, currentShrinkingTime);

			if (capturing) { //Capturing
				fraction = interpolatedShrinkingTime / (float) pEntity.getAnimationTicks();
				originalPosition = pEntity.getMobPosition();
			} else { //Releasing
				fraction = (pEntity.getAnimationTicks() - interpolatedShrinkingTime) / (float) pEntity.getAnimationTicks();
				originalPosition = entityPosition;
			}
			// Flattens the shrinkage rate when near 0
			fraction = Mth.cos(fraction * Mth.PI) * -0.5f + 0.5f;

			// lerp changes the vector in place
			Vector3f interpolatedPosition = originalPosition.copy();
			interpolatedPosition.lerp(entityPosition, fraction);

			pPoseStack.translate(interpolatedPosition.x() - pEntity.getX(), interpolatedPosition.y() - pEntity.getY(), interpolatedPosition.z() - pEntity.getZ());

			float bigScale = 1;
			float smallScale = 0.2f;
			float scale = Mth.clampedLerp(bigScale, smallScale, fraction);
			pPoseStack.scale(scale, scale, scale);

			EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
			EntityRenderer<? super Mob> renderer = dispatcher.getRenderer(mob);
			renderer.render(mob, mob.yBodyRot, pPartialTicks, pPoseStack, pBuffer, pPackedLight);

			pPoseStack.popPose();
		}
	}
}
