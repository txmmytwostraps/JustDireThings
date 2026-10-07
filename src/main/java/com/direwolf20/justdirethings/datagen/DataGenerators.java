package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = JustDireThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		if (event.includeServer()) {
			generator.addProvider(new JustDireRecipes(generator));
			generator.addProvider(new JustDireLootTables(generator));
			JustDireBlockTags blockTags = new JustDireBlockTags(generator, event.getExistingFileHelper());
			generator.addProvider(blockTags);
			generator.addProvider(new JustDireItemTags(generator, blockTags, event.getExistingFileHelper()));
			generator.addProvider(new JustDireEntityTags(generator, event.getExistingFileHelper()));
			generator.addProvider(new JustDireFluidTags(generator, event.getExistingFileHelper()));
			generator.addProvider(new JustDireBiomeTags(generator, event.getExistingFileHelper()));
		}
		if (event.includeClient()) {
			generator.addProvider(new JustDireBlockStates(generator, event.getExistingFileHelper()));
			generator.addProvider(new JustDireItemModels(generator, event.getExistingFileHelper()));
			generator.addProvider(new JustDireLanguageProvider(generator, "en_us"));
		}
	}
}
