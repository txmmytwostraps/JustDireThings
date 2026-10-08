package com.direwolf20.justdirethings.client.particles;


import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.particles.alwaysvisibleparticle.AlwaysVisibleParticleType;
import com.direwolf20.justdirethings.client.particles.glitterparticle.GlitterParticleType;
import com.direwolf20.justdirethings.client.particles.gooexplodeparticle.GooExplodeParticleType;
import com.direwolf20.justdirethings.client.particles.itemparticle.ItemFlowParticleType;
import com.direwolf20.justdirethings.client.particles.paradoxparticle.ParadoxParticleType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class ModParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, JustDireThings.MODID);
	public static final RegistryObject<GooExplodeParticleType> GOOEXPLODEPARTICLE = PARTICLE_TYPES.register("gooexplodeparticle", () -> new GooExplodeParticleType(false));
	public static final RegistryObject<ItemFlowParticleType> ITEMFLOWPARTICLE = PARTICLE_TYPES.register("itemflowparticle", () -> new ItemFlowParticleType(false));
	public static final RegistryObject<AlwaysVisibleParticleType> ALWAYSVISIBLEPARTICLE = PARTICLE_TYPES.register("alwaysvisibleparticle", () -> new AlwaysVisibleParticleType(false));
	public static final RegistryObject<GlitterParticleType> GLITTERPARTICLE = PARTICLE_TYPES.register("glitter", () -> new GlitterParticleType(false));
	public static final RegistryObject<ParadoxParticleType> PARADOXPARTICLE = PARTICLE_TYPES.register("paradox", () -> new ParadoxParticleType(false));

	private static final DynamicCommandExceptionType ERROR_INVALID_OPTIONS = new DynamicCommandExceptionType(error -> new TranslatableComponent("particle.invalidOptions", error));

	// Options after a particle id, from commands and saved clouds. The compound is optional.
	public static <T extends ParticleOptions> T readOptions(StringReader reader, Codec<T> codec) throws CommandSyntaxException {
		CompoundTag tag = reader.canRead() && reader.peek() == '{' ? new TagParser(reader).readStruct() : new CompoundTag();
		DataResult<T> result = codec.parse(NbtOps.INSTANCE, tag);
		Optional<DataResult.PartialResult<T>> error = result.error();
		if (error.isPresent())
			throw ERROR_INVALID_OPTIONS.create(error.get().message());
		return result.result().get();
	}

	// Same format readOptions reads
	public static <T extends ParticleOptions> String writeOptions(T options, Codec<T> codec) {
		return ForgeRegistries.PARTICLE_TYPES.getKey(options.getType()) + codec.encodeStart(NbtOps.INSTANCE, options).result().map(Tag::getAsString).orElse("{}");
	}
}
