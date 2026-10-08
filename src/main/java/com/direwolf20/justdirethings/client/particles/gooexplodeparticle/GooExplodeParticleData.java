package com.direwolf20.justdirethings.client.particles.gooexplodeparticle;

import com.direwolf20.justdirethings.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class GooExplodeParticleData implements ParticleOptions {
	public static final Codec<GooExplodeParticleData> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					ItemStack.CODEC.fieldOf("itemStack").forGetter(GooExplodeParticleData::getItemStack)
			).apply(instance, GooExplodeParticleData::new));
	public static final ParticleOptions.Deserializer<GooExplodeParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Nonnull
		@Override
		public GooExplodeParticleData fromCommand(ParticleType<GooExplodeParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return ModParticles.readOptions(reader, CODEC);
		}

		@Override
		public GooExplodeParticleData fromNetwork(ParticleType<GooExplodeParticleData> particleTypeIn, FriendlyByteBuf buffer) {
			return new GooExplodeParticleData(buffer.readItem());
		}
	};

	private final ItemStack itemStack;

	public GooExplodeParticleData(ItemStack itemStack) {
		this.itemStack = itemStack.copy(); //Forge: Fix stack updating after the fact causing particle changes.
	}

	@Nonnull
	@Override
	public ParticleType<GooExplodeParticleData> getType() {
		return ModParticles.GOOEXPLODEPARTICLE.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeItem(this.itemStack);
	}

	@Nonnull
	@Override
	public String writeToString() {
		return ModParticles.writeOptions(this, CODEC);
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}
}
