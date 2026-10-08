package com.direwolf20.justdirethings.client.particles.paradoxparticle;

import com.direwolf20.justdirethings.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.SerializableUUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ParadoxParticleData implements ParticleOptions {
	public static final Codec<ParadoxParticleData> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					ItemStack.CODEC.fieldOf("itemStack").forGetter(p -> p.itemStack),
					Codec.DOUBLE.fieldOf("targetX").forGetter(p -> p.targetX),
					Codec.DOUBLE.fieldOf("targetY").forGetter(p -> p.targetY),
					Codec.DOUBLE.fieldOf("targetZ").forGetter(p -> p.targetZ),
					Codec.INT.fieldOf("ticksPerBlock").forGetter(p -> p.ticksPerBlock),
					SerializableUUID.CODEC.fieldOf("paradox_uuid").forGetter(p -> p.paradox_uuid)
			).apply(instance, ParadoxParticleData::new));
	public static final ParticleOptions.Deserializer<ParadoxParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Nonnull
		@Override
		public ParadoxParticleData fromCommand(ParticleType<ParadoxParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return ModParticles.readOptions(reader, CODEC);
		}

		@Override
		public ParadoxParticleData fromNetwork(ParticleType<ParadoxParticleData> particleTypeIn, FriendlyByteBuf buffer) {
			return new ParadoxParticleData(buffer.readItem(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt(), buffer.readUUID());
		}
	};

	private final ItemStack itemStack;
	public final double targetX;
	public final double targetY;
	public final double targetZ;
	public final int ticksPerBlock;
	public final UUID paradox_uuid;

	public ParadoxParticleData(ItemStack itemStack, double tx, double ty, double tz, int ticks, UUID paradox_uuid) {
		this.itemStack = itemStack.copy(); //Forge: Fix stack updating after the fact causing particle changes.
		targetX = tx;
		targetY = ty;
		targetZ = tz;
		ticksPerBlock = ticks;
		this.paradox_uuid = paradox_uuid;
	}

	@Nonnull
	@Override
	public ParticleType<ParadoxParticleData> getType() {
		return ModParticles.PARADOXPARTICLE.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeItem(this.itemStack);
		buffer.writeDouble(this.targetX);
		buffer.writeDouble(this.targetY);
		buffer.writeDouble(this.targetZ);
		buffer.writeInt(this.ticksPerBlock);
		buffer.writeUUID(this.paradox_uuid);
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
