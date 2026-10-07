package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class JustDireBiomeTags extends BiomeTagsProvider {
	public JustDireBiomeTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, JustDireThings.MODID, existingFileHelper);
	}

	public static final TagKey<Biome> UNSTABLE_PORTAL_FLUID_VIABLE = TagKey.create(Registry.BIOME_REGISTRY,
			ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "unstable_portal_fluid_viable"));

	@Override
	protected void addTags() {
		tag(UNSTABLE_PORTAL_FLUID_VIABLE).add(Biomes.END_BARRENS, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS,
				Biomes.SMALL_END_ISLANDS, Biomes.THE_END);
	}

	@Override
	public String getName() {
		return "JustDireThings Biome Tags";
	}
}
