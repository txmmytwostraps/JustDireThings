package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.client.OurSounds;
import com.direwolf20.justdirethings.common.network.data.ClientSoundPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ClientSoundPacket {
	public static final ClientSoundPacket INSTANCE = new ClientSoundPacket();

	public static ClientSoundPacket get() {
		return INSTANCE;
	}

	public void handle(final ClientSoundPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(payload.soundEvent());
			if (soundEvent != null) {
				OurSounds.playSound(soundEvent, payload.pitch(), payload.volume());
			}
		}));
		ctx.get().setPacketHandled(true);
	}
}
