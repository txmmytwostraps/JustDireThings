package com.direwolf20.justdirethings.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

import java.awt.*;

public class RenderHelpers {
	private static final float dummyU0 = 0F;
	private static final float dummyU1 = 1F;
	private static final float dummyV0 = 0F;
	private static final float dummyV1 = 1F;

	public static void renderLines(PoseStack matrix, BlockPos startPos, BlockPos endPos, Color color, MultiBufferSource buffer) {
		//We want to draw from the starting position to the (ending position)+1
		int x = Math.min(startPos.getX(), endPos.getX()), y = Math.min(startPos.getY(), endPos.getY()), z = Math.min(startPos.getZ(), endPos.getZ());

		int dx = (startPos.getX() > endPos.getX()) ? startPos.getX() + 1 : endPos.getX() + 1;
		int dy = (startPos.getY() > endPos.getY()) ? startPos.getY() + 1 : endPos.getY() + 1;
		int dz = (startPos.getZ() > endPos.getZ()) ? startPos.getZ() + 1 : endPos.getZ() + 1;

		VertexConsumer builder = buffer.getBuffer(OurRenderTypes.lines());

		matrix.pushPose();
		Matrix4f matrix4f = matrix.last().pose();
		Matrix3f matrix3f = matrix.last().normal();
		int colorRGB = color.getRGB();

		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();

		matrix.popPose();
	}

	public static void renderLines(PoseStack matrix, AABB aabb, Color color, MultiBufferSource buffer) {
		//We want to draw from the starting position to the (ending position)+1
		float x = (float) aabb.minX;
		float y = (float) aabb.minY;
		float z = (float) aabb.minZ;
		float dx = (float) aabb.maxX;
		float dy = (float) aabb.maxY;
		float dz = (float) aabb.maxZ;

		VertexConsumer builder = buffer.getBuffer(OurRenderTypes.lines());

		matrix.pushPose();
		Matrix4f matrix4f = matrix.last().pose();
		Matrix3f matrix3f = matrix.last().normal();
		int colorRGB = color.getRGB();

		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, x, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, dx, y, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
		builder.vertex(matrix4f, x, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, y, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, z).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(matrix4f, dx, dy, dz).color(colorRGB).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();

		matrix.popPose();
	}

	public static void renderBoxSolid(PoseStack pose, Matrix4f matrix, MultiBufferSource buffer, BlockPos pos, float r, float g, float b, float alpha) {
		double x = pos.getX() - 0.001;
		double y = pos.getY() - 0.001;
		double z = pos.getZ() - 0.001;
		double xEnd = pos.getX() + 1.0015;
		double yEnd = pos.getY() + 1.0015;
		double zEnd = pos.getZ() + 1.0015;

		renderBoxSolid(pose.last(), matrix, buffer, x, y, z, xEnd, yEnd, zEnd, r, g, b, alpha);
	}

	public static void renderFaceSolid(PoseStack pose, Matrix4f matrix, MultiBufferSource buffer, BlockPos pos, Direction direction, float r, float g, float b, float alpha) {
		double x = pos.getX() - 0.001;
		double y = pos.getY() - 0.001;
		double z = pos.getZ() - 0.001;
		double xEnd = pos.getX() + 1.0015;
		double yEnd = pos.getY() + 1.0015;
		double zEnd = pos.getZ() + 1.0015;

		switch (direction) {
			case DOWN:
				// Draw on the bottom face (y = pos.getY())
				renderBoxSolid(pose.last(), matrix, buffer, x, y - 0.001, z, xEnd, y, zEnd, r, g, b, alpha);
				break;
			case UP:
				// Draw on the top face (y = pos.getY() + 1)
				renderBoxSolid(pose.last(), matrix, buffer, x, yEnd, z, xEnd, yEnd + 0.0015, zEnd, r, g, b, alpha);
				break;
			case NORTH:
				// Draw on the north face (z = pos.getZ())
				renderBoxSolid(pose.last(), matrix, buffer, x, y, z - 0.001, xEnd, yEnd, z, r, g, b, alpha);
				break;
			case SOUTH:
				// Draw on the south face (z = pos.getZ() + 1)
				renderBoxSolid(pose.last(), matrix, buffer, x, y, zEnd, xEnd, yEnd, zEnd + 0.0015, r, g, b, alpha);
				break;
			case WEST:
				// Draw on the west face (x = pos.getX())
				renderBoxSolid(pose.last(), matrix, buffer, x - 0.001, y, z, x, yEnd, zEnd, r, g, b, alpha);
				break;
			case EAST:
				// Draw on the east face (x = pos.getX() + 1)
				renderBoxSolid(pose.last(), matrix, buffer, xEnd, y, z, xEnd + 0.0015, yEnd, zEnd, r, g, b, alpha);
				break;
		}
	}

	public static void renderBoxSolid(PoseStack pose, Matrix4f matrix, MultiBufferSource buffer, AABB aabb, float r, float g, float b, float alpha) {
		float minX = (float) aabb.minX;
		float minY = (float) aabb.minY;
		float minZ = (float) aabb.minZ;
		float maxX = (float) aabb.maxX;
		float maxY = (float) aabb.maxY;
		float maxZ = (float) aabb.maxZ;

		renderBoxSolid(pose.last(), matrix, buffer, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, alpha);
	}

	//This one does not block water
	public static void renderBoxSolid(PoseStack.Pose pose, Matrix4f matrix, MultiBufferSource buffer, double x, double y, double z, double xEnd, double yEnd, double zEnd, float red, float green, float blue, float alpha) {
		VertexConsumer builder = buffer.getBuffer(OurRenderTypes.TRANSPARENT_BOX);

		//careful: mc want's it's vertices to be defined CCW - if you do it the other way around weird cullling issues will arise
		//CCW herby counts as if you were looking at it from the outside
		float startX = (float) x;
		float startY = (float) y;
		float startZ = (float) z;
		float endX = (float) xEnd;
		float endY = (float) yEnd;
		float endZ = (float) zEnd;

		//down
		builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).uv(dummyU0, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).uv(dummyU0, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).uv(dummyU1, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).uv(dummyU1, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();

		//up
		builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).uv(dummyU0, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).uv(dummyU0, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).uv(dummyU1, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).uv(dummyU1, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();

		//east
		builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).uv(dummyU0, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).uv(dummyU0, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).uv(dummyU1, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).uv(dummyU1, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();

		//west
		builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).uv(dummyU0, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).uv(dummyU0, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).uv(dummyU1, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).uv(dummyU1, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();

		//south
		builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).uv(dummyU0, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).uv(dummyU0, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).uv(dummyU1, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).uv(dummyU1, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();

		//north
		builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).uv(dummyU0, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).uv(dummyU0, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).uv(dummyU1, dummyV1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
		builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).uv(dummyU1, dummyV0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0F, 0F, 1F).endVertex();
	}

	public static void renderSphere(PoseStack poseStack, MultiBufferSource bufferSource, Color color, float radius, int light) {
		VertexConsumer builder = bufferSource.getBuffer(OurRenderTypes.TRIANGLE_STRIP);
		Matrix4f matrix = poseStack.last().pose();
		PoseStack.Pose matrixPose = poseStack.last();

		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		int alpha = color.getAlpha();

		int latitudeBands = 16;
		int longitudeBands = 16;

		for (int latNumber = 0; latNumber <= latitudeBands; latNumber++) {
			float theta1 = (float) (latNumber * Math.PI / latitudeBands);
			float theta2 = (float) ((latNumber + 1) * Math.PI / latitudeBands);

			float sinTheta1 = Mth.sin(theta1);
			float cosTheta1 = Mth.cos(theta1);
			float sinTheta2 = Mth.sin(theta2);
			float cosTheta2 = Mth.cos(theta2);

			for (int longNumber = 0; longNumber <= longitudeBands; longNumber++) {
				float phi = (float) (longNumber * 2 * Math.PI / longitudeBands);
				float sinPhi = Mth.sin(phi);
				float cosPhi = Mth.cos(phi);

				float x1 = cosPhi * sinTheta1;
				float y1 = cosTheta1;
				float z1 = sinPhi * sinTheta1;

				float x2 = cosPhi * sinTheta2;
				float y2 = cosTheta2;
				float z2 = sinPhi * sinTheta2;

				builder.vertex(matrix, x1 * radius, y1 * radius, z1 * radius)
						.color(red, green, blue, alpha)
						.uv(dummyU0, dummyV0)
						.overlayCoords(OverlayTexture.NO_OVERLAY)
						.uv2(light)
						.normal(matrixPose.normal(), x1, y1, z1)
						.endVertex();

				builder.vertex(matrix, x2 * radius, y2 * radius, z2 * radius)
						.color(red, green, blue, alpha)
						.uv(dummyU1, dummyV1)
						.overlayCoords(OverlayTexture.NO_OVERLAY)
						.uv2(light)
						.normal(matrixPose.normal(), x2, y2, z2)
						.endVertex();
			}
		}
	}
}
