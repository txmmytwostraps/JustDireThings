package com.direwolf20.justdirethings.client.particles.paradoxparticle;


import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class ParadoxParticleType extends ParticleType<ParadoxParticleData> {
	public ParadoxParticleType(boolean pOverrideLimiter) {
		super(pOverrideLimiter, ParadoxParticleData.DESERIALIZER);
	}

	@Override
	public Codec<ParadoxParticleData> codec() {
		return ParadoxParticleData.CODEC;
	}

}
