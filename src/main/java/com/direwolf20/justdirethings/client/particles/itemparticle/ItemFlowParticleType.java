package com.direwolf20.justdirethings.client.particles.itemparticle;


import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class ItemFlowParticleType extends ParticleType<ItemFlowParticleData> {
	public ItemFlowParticleType(boolean pOverrideLimiter) {
		super(pOverrideLimiter, ItemFlowParticleData.DESERIALIZER);
	}

	@Override
	public Codec<ItemFlowParticleData> codec() {
		return ItemFlowParticleData.CODEC;
	}

}
