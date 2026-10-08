package com.direwolf20.justdirethings.client.particles.glitterparticle;

import com.direwolf20.justdirethings.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;

public class GlitterParticleData implements ParticleOptions {
	public static final Codec<GlitterParticleData> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.STRING.fieldOf("partType").forGetter(p -> p.partType),
					Codec.DOUBLE.fieldOf("targetX").forGetter(p -> p.targetX),
					Codec.DOUBLE.fieldOf("targetY").forGetter(p -> p.targetY),
					Codec.DOUBLE.fieldOf("targetZ").forGetter(p -> p.targetZ),
					Codec.FLOAT.fieldOf("size").forGetter(p -> p.size),
					Codec.FLOAT.fieldOf("r").forGetter(p -> p.r),
					Codec.FLOAT.fieldOf("g").forGetter(p -> p.g),
					Codec.FLOAT.fieldOf("b").forGetter(p -> p.b),
					Codec.FLOAT.fieldOf("a").forGetter(p -> p.a),
					Codec.FLOAT.fieldOf("maxAgeMul").forGetter(p -> p.maxAgeMul),
					Codec.BOOL.fieldOf("depthTest").forGetter(p -> p.depthTest)
			).apply(instance, GlitterParticleData::new));
	public static final ParticleOptions.Deserializer<GlitterParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Nonnull
		@Override
		public GlitterParticleData fromCommand(ParticleType<GlitterParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return ModParticles.readOptions(reader, CODEC);
		}

		@Override
		public GlitterParticleData fromNetwork(ParticleType<GlitterParticleData> particleTypeIn, FriendlyByteBuf buffer) {
			return new GlitterParticleData(buffer.readUtf(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readBoolean());
		}
	};

	public final float size;
	public final float r, g, b, a;
	public final float maxAgeMul;
	public final boolean depthTest;
	public final double targetX;
	public final double targetY;
	public final double targetZ;
	public final String partType;

	public static GlitterParticleData playerparticle(String type, double targetX, double targetY, double targetZ, float size, float r, float g, float b, float a, float maxAgeMul, boolean depthTest) {
		return new GlitterParticleData(type, targetX, targetY, targetZ, size, r, g, b, a, maxAgeMul, depthTest);
	}

	private GlitterParticleData(String type, double targetX, double targetY, double targetZ, float size, float r, float g, float b, float a, float maxAgeMul, boolean depthTest) {
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.size = size;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.maxAgeMul = maxAgeMul;
		this.depthTest = depthTest;
		this.partType = type;
	}

	@Nonnull
	@Override
	public ParticleType<GlitterParticleData> getType() {
		return ModParticles.GLITTERPARTICLE.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeUtf(partType);
		buffer.writeDouble(targetX);
		buffer.writeDouble(targetY);
		buffer.writeDouble(targetZ);
		buffer.writeFloat(size);
		buffer.writeFloat(r);
		buffer.writeFloat(g);
		buffer.writeFloat(b);
		buffer.writeFloat(a);
		buffer.writeFloat(maxAgeMul);
		buffer.writeBoolean(depthTest);
	}

	@Nonnull
	@Override
	public String writeToString() {
		return ModParticles.writeOptions(this, CODEC);
	}
}
