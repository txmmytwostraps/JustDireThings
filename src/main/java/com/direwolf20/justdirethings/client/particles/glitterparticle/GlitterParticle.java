package com.direwolf20.justdirethings.client.particles.glitterparticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class GlitterParticle extends TextureSheetParticle {
	private double targetX;
	private double targetY;
	private double targetZ;
	private Random rand = new Random();
	private int particlePicker;

	public GlitterParticle(ClientLevel world, double sourceX, double sourceY, double sourceZ, double targetX, double targetY, double targetZ, double xSpeed, double ySpeed, double zSpeed,
			float size, float red, float green, float blue, float alpha, boolean collide, float maxAge, String particleType, SpriteSet sprite) {
		super(world, sourceX, sourceY, sourceZ);
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
		rCol = red;
		gCol = green;
		bCol = blue;
		this.alpha = alpha;
		gravity = 0;
		this.lifetime = Math.round(maxAge);

		setSize(0.001F, 0.001F);

		xo = x;
		yo = y;
		zo = z;
		this.quadSize = size;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.hasPhysics = collide;
		this.setGravity(0f);
		particlePicker = rand.nextInt(3) + 1;
		this.setSprite(sprite.get(particlePicker, 4));
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		double moveX;
		double moveY;
		double moveZ;

		//Just in case something goes weird, we remove the particle if its been around too long.
		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		//prevPos is used in the render. if you don't do this your particle rubber bands (Like lag in an MMO).
		//This is used because ticks are 20 per second, and FPS is usually 60 or higher.
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		Vec3 targetPos = new Vec3(targetX, targetY, targetZ);

		Vec3 partPos = new Vec3(this.x, this.y, this.z);

		//The total distance between the particle and target
		double totalDistance = targetPos.distanceTo(partPos);
		if (totalDistance < 0.1)
			this.remove();

		double speedAdjust = 20;

		moveX = (targetX - this.x) / speedAdjust;
		moveY = (targetY - this.y) / speedAdjust;
		moveZ = (targetZ - this.z) / speedAdjust;

		if (age > 40)
			this.hasPhysics = false;
		this.move(moveX, moveY, moveZ);
	}

	public void setGravity(float value) {
		gravity = value;
	}

}
