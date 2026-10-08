package com.direwolf20.justdirethings.client.renderers;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.entities.CreatureCatcherEntity;
import com.direwolf20.justdirethings.common.items.CreatureCatcher;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FrameTimer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import static net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect;

public class JustDireItemRenderer extends BlockEntityWithoutLevelRenderer {
	public JustDireItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	private static final ResourceLocation CREATURE_CATCHER_BASE = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "item/creaturecatcher_base");



	@Override
	public void renderByItem(ItemStack pStack, ItemTransforms.TransformType pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		if (pStack.getItem() instanceof CreatureCatcher) {
			ItemRenderer irenderer = Minecraft.getInstance().getItemRenderer();
			BakedModel base = irenderer.getItemModelShaper().getModelManager().getModel(CREATURE_CATCHER_BASE);
			pPoseStack.pushPose();
			if (pDisplayContext != ItemTransforms.TransformType.GUI) { //Ground/Hand/etc?
				if (pDisplayContext == ItemTransforms.TransformType.FIXED) {
					pPoseStack.translate(0.0, 0.0, 0.5);
					float scale = 1F;
					pPoseStack.scale(scale, scale, scale);
					pPoseStack.mulPose(Vector3f.XN.rotationDegrees(90));
				} else {
					pPoseStack.translate(0.2, 0.5, 0.5);
					float scale = 0.4F;
					pPoseStack.scale(scale, scale, scale);
					pPoseStack.translate(0F, 0F, 0F);
					pPoseStack.mulPose(Vector3f.YP.rotationDegrees(45));
				}
			} else { //GUI?
				pPoseStack.translate(0.95F, 0.25F, 0);
				pPoseStack.mulPose(Vector3f.XP.rotationDegrees(30));
				pPoseStack.mulPose(Vector3f.YP.rotationDegrees(225));
				float scale = 0.625F;
				pPoseStack.scale(scale, scale, scale);
				pPoseStack.translate(0, 0, 0);
			}
			VertexConsumer vertexconsumer = getFoilBufferDirect(pBuffer, ItemBlockRenderTypes.getRenderType(pStack, true), true, pStack.hasFoil());
			irenderer.renderModelLists(base, pStack, pPackedLight, pPackedOverlay, pPoseStack, vertexconsumer);
			pPoseStack.popPose();

			Mob mob = CreatureCatcherEntity.getEntityFromItemStack(pStack, Minecraft.getInstance().level);
			if (mob != null)
				renderEntityInInventory(pPoseStack, pDisplayContext, mob, pBuffer);
		}
	}

	public void renderEntityInInventory(PoseStack matrix, ItemTransforms.TransformType type, LivingEntity pLivingEntity, MultiBufferSource pBuffer) {
		matrix.pushPose();
		matrix.translate(0.5, 0.5, 0.5);

		// Get entity dimensions
		AABB boundingBox = pLivingEntity.getBoundingBox();
		double entityHeight = boundingBox.maxY - boundingBox.minY;
		double entityWidth = Math.max(boundingBox.maxX - boundingBox.minX, boundingBox.maxZ - boundingBox.minZ);

		// Determine scaling factor
		double maxDimension = Math.max(entityWidth, entityHeight);
		float scale = 0.25F / (float) maxDimension; // Adjust this factor based on your UI needs


		if (type == ItemTransforms.TransformType.FIXED) {
			matrix.translate(0.0, 0.0, -0.125);
			matrix.mulPose(Vector3f.XN.rotationDegrees(90));
			matrix.mulPose(Vector3f.YN.rotationDegrees(180));
			matrix.scale(scale * 2, scale * 2, scale * 2);
		} else if (type == ItemTransforms.TransformType.GUI) {
			matrix.translate(0, -entityHeight / 2 * scale * 2, 0); // Centering entity vertically
			matrix.scale(scale * 2, scale * 2, scale * 2);
		} else { //In hand / On ground
			matrix.translate(0, 0.02, 0);
			matrix.scale(scale, scale, scale);
		}

		float rotation = -30;
		if (type == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND || type == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
			rotation = 30;
		if (type == ItemTransforms.TransformType.FIXED) rotation = 180;
		matrix.mulPose(Vector3f.YP.rotationDegrees(rotation));
		pLivingEntity.setYRot(0);
		pLivingEntity.yBodyRot = pLivingEntity.getYRot();
		pLivingEntity.yHeadRot = pLivingEntity.getYRot();
		pLivingEntity.yHeadRotO = pLivingEntity.getYRot();
		EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		entityrenderermanager.setRenderShadow(false);
		// Last frame's length in nanoseconds drives the idle animation
		FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
		float lastFrameNanos = frameTimer.getLog()[frameTimer.wrapIndex(frameTimer.getLogEnd() + FrameTimer.LOGGING_LENGTH - 1)];
		RenderSystem.runAsFancy(() -> {
			entityrenderermanager.render(pLivingEntity, 0, 0, 0, 0.0F, lastFrameNanos, matrix, pBuffer, 15728880);
		});
		entityrenderermanager.setRenderShadow(true);
		matrix.popPose();
	}
}
