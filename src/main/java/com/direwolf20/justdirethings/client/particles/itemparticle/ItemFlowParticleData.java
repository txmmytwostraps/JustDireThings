package com.direwolf20.justdirethings.client.particles.itemparticle;

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

public class ItemFlowParticleData implements ParticleOptions {
	public static final Codec<ItemFlowParticleData> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					ItemStack.CODEC.fieldOf("itemStack").forGetter(p -> p.itemStack),
					Codec.DOUBLE.fieldOf("targetX").forGetter(p -> p.targetX),
					Codec.DOUBLE.fieldOf("targetY").forGetter(p -> p.targetY),
					Codec.DOUBLE.fieldOf("targetZ").forGetter(p -> p.targetZ),
					Codec.INT.fieldOf("ticksPerBlock").forGetter(p -> p.ticksPerBlock)
			).apply(instance, ItemFlowParticleData::new));
	public static final ParticleOptions.Deserializer<ItemFlowParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Nonnull
		@Override
		public ItemFlowParticleData fromCommand(ParticleType<ItemFlowParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return ModParticles.readOptions(reader, CODEC);
		}

		@Override
		public ItemFlowParticleData fromNetwork(ParticleType<ItemFlowParticleData> particleTypeIn, FriendlyByteBuf buffer) {
			return new ItemFlowParticleData(buffer.readItem(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt());
		}
	};

	private final ItemStack itemStack;
	public final double targetX;
	public final double targetY;
	public final double targetZ;
	public final int ticksPerBlock;

	public ItemFlowParticleData(ItemStack itemStack, double tx, double ty, double tz, int ticks) {
		this.itemStack = itemStack.copy(); //Forge: Fix stack updating after the fact causing particle changes.
		targetX = tx;
		targetY = ty;
		targetZ = tz;
		ticksPerBlock = ticks;
	}

	@Nonnull
	@Override
	public ParticleType<ItemFlowParticleData> getType() {
		return ModParticles.ITEMFLOWPARTICLE.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeItem(this.itemStack);
		buffer.writeDouble(this.targetX);
		buffer.writeDouble(this.targetY);
		buffer.writeDouble(this.targetZ);
		buffer.writeInt(this.ticksPerBlock);
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
