package com.direwolf20.justdirethings.client.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Remaps UV coordinates from a source sprite's atlas region to a target
 * sprite's atlas region. Used to render the goo spread pattern geometry with
 * the target block's texture instead of the pattern block's texture, enabling a
 * shader-compatible visual match.
 */
public class DireUVRemapVertexConsumer implements VertexConsumer {
	private final VertexConsumer parent;
	private final TextureAtlasSprite sourceSprite;
	private final TextureAtlasSprite targetSprite;

	public DireUVRemapVertexConsumer(VertexConsumer parent, TextureAtlasSprite sourceSprite,
			TextureAtlasSprite targetSprite) {
		this.parent = parent;
		this.sourceSprite = sourceSprite;
		this.targetSprite = targetSprite;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		parent.vertex(x, y, z);
		return this;
	}

	@Override
	public VertexConsumer color(int r, int g, int b, int a) {
		parent.color(r, g, b, a);
		return this;
	}

	@Override
	public VertexConsumer uv(float u, float v) {
		float sourceWidth = sourceSprite.getU1() - sourceSprite.getU0();
		float sourceHeight = sourceSprite.getV1() - sourceSprite.getV0();
		float normalU = sourceWidth == 0f ? 0f : (u - sourceSprite.getU0()) / sourceWidth;
		float normalV = sourceHeight == 0f ? 0f : (v - sourceSprite.getV0()) / sourceHeight;
		float newU = targetSprite.getU0() + normalU * (targetSprite.getU1() - targetSprite.getU0());
		float newV = targetSprite.getV0() + normalV * (targetSprite.getV1() - targetSprite.getV0());
		parent.uv(newU, newV);
		return this;
	}

	@Override
	public VertexConsumer overlayCoords(int u, int v) {
		parent.overlayCoords(u, v);
		return this;
	}

	@Override
	public VertexConsumer uv2(int u, int v) {
		parent.uv2(u, v);
		return this;
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		parent.normal(x, y, z);
		return this;
	}

	@Override
	public void endVertex() {
		parent.endVertex();
	}

	@Override
	public void defaultColor(int r, int g, int b, int a) {
		parent.defaultColor(r, g, b, a);
	}

	@Override
	public void unsetDefaultColor() {
		parent.unsetDefaultColor();
	}

	@Override
	public VertexFormat getVertexFormat() {
		return parent.getVertexFormat();
	}
}
