package com.direwolf20.justdirethings.client.particles.alwaysvisibleparticle;

import com.direwolf20.justdirethings.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class AlwaysVisibleParticleData implements ParticleOptions {
	public static final Codec<AlwaysVisibleParticleData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							ResourceLocation.CODEC.fieldOf("resourceLocation").forGetter(AlwaysVisibleParticleData::getResourceLocation)
					)
					.apply(instance, AlwaysVisibleParticleData::new)
	);
	public static final ParticleOptions.Deserializer<AlwaysVisibleParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Override
		public AlwaysVisibleParticleData fromCommand(ParticleType<AlwaysVisibleParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return ModParticles.readOptions(reader, CODEC);
		}

		@Override
		public AlwaysVisibleParticleData fromNetwork(ParticleType<AlwaysVisibleParticleData> particleTypeIn, FriendlyByteBuf buffer) {
			return new AlwaysVisibleParticleData(buffer.readResourceLocation());
		}
	};

	ResourceLocation resourceLocation;

	public AlwaysVisibleParticleData(ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	@Override
	public ParticleType<?> getType() {
		return ModParticles.ALWAYSVISIBLEPARTICLE.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(resourceLocation);
	}

	@Override
	public String writeToString() {
		return ModParticles.writeOptions(this, CODEC);
	}

}
