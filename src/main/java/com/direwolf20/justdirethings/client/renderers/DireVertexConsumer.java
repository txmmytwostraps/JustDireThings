package com.direwolf20.justdirethings.client.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;


public class DireVertexConsumer implements VertexConsumer {
	private final VertexConsumer parent;
	private float alpha;

	public DireVertexConsumer(VertexConsumer parent, float alpha) {
		this.parent = parent;
		this.alpha = alpha;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		parent.vertex(x, y, z);
		return this;
	}

	@Override
	public VertexConsumer color(int r, int g, int b, int a) {
		parent.color(r, g, b, Math.round((float) 255 * alpha));
		return this;
	}

	@Override
	public VertexConsumer uv(float u, float v) {
		parent.uv(u, v);
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
