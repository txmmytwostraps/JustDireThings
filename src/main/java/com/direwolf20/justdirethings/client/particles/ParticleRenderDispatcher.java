package com.direwolf20.justdirethings.client.particles;


import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.particles.alwaysvisibleparticle.AlwaysVisibleParticle;
import com.direwolf20.justdirethings.client.particles.glitterparticle.GlitterParticleType;
import com.direwolf20.justdirethings.client.particles.gooexplodeparticle.GooExplodeParticle;
import com.direwolf20.justdirethings.client.particles.itemparticle.ItemFlowParticle;
import com.direwolf20.justdirethings.client.particles.paradoxparticle.ParadoxParticle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JustDireThings.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRenderDispatcher {

	@SubscribeEvent
	public static void registerProviders(ParticleFactoryRegisterEvent evt) {
		Minecraft.getInstance().particleEngine.register(ModParticles.GOOEXPLODEPARTICLE.get(), GooExplodeParticle.FACTORY);
		Minecraft.getInstance().particleEngine.register(ModParticles.ITEMFLOWPARTICLE.get(), ItemFlowParticle.FACTORY);
		Minecraft.getInstance().particleEngine.register(ModParticles.ALWAYSVISIBLEPARTICLE.get(), AlwaysVisibleParticle.FACTORY);
		Minecraft.getInstance().particleEngine.register(ModParticles.GLITTERPARTICLE.get(), GlitterParticleType.FACTORY::new);
		Minecraft.getInstance().particleEngine.register(ModParticles.PARADOXPARTICLE.get(), ParadoxParticle.FACTORY);
	}
}
