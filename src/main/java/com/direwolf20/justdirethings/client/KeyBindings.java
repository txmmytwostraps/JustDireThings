package com.direwolf20.justdirethings.client;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindings {
	private static final Logger LOGGER = LoggerFactory.getLogger(KeyBindings.class);
	private static final KeyConflictContextGadget CONFLICT_CONTEXT_GADGET = new KeyConflictContextGadget();
	private static final KeyConflictContextConfigScreen CONFLICT_CONTEXT_CONFIG = new KeyConflictContextConfigScreen();

	private static final List<KeyMapping> keyMappings = new ArrayList<>();

	public static KeyMapping toggleTool = createBinding("toggle_tool", GLFW.GLFW_KEY_V);
	public static KeyMapping toolUI = createBindingGUI("toolUI", -1);

	private static KeyMapping createBinding(String name, int key) {
		KeyMapping keyBinding = new KeyMapping(getKey(name), CONFLICT_CONTEXT_GADGET, InputConstants.Type.KEYSYM.getOrCreate(key), getKey("category"));
		keyMappings.add(keyBinding);
		return keyBinding;
	}

	private static KeyMapping createBindingGUI(String name, int key) {
		KeyMapping keyBinding = new KeyMapping(getKey(name), CONFLICT_CONTEXT_CONFIG, InputConstants.Type.KEYSYM.getOrCreate(key), getKey("category"));
		keyMappings.add(keyBinding);
		return keyBinding;
	}

	@SubscribeEvent
	public static void registerKeyMappings(FMLClientSetupEvent event) {
		LOGGER.debug("Registering {} keybinding for {}", keyMappings.size(), JustDireThings.MODID);
		keyMappings.forEach(ClientRegistry::registerKeyBinding);
	}

	private static String getKey(String name) {
		return String.join(".", JustDireThings.MODID, "key", name);
	}

	public static void onClientInput(InputEvent.KeyInputEvent event) {
	}

	public static class KeyConflictContextGadget implements IKeyConflictContext {
		@Override
		public boolean isActive() {
			Player player = Minecraft.getInstance().player;
			return !KeyConflictContext.GUI.isActive() && player != null
					&& ((!ToggleableItem.getToggleableItem(player).isEmpty()) || (!PortalGunV2.getPortalGunv2(player).isEmpty()));
		}

		@Override
		public boolean conflicts(IKeyConflictContext other) {
			return other == this || other == KeyConflictContext.IN_GAME;
		}
	}

	public static class KeyConflictContextConfigScreen implements IKeyConflictContext {
		@Override
		public boolean isActive() {
			Player player = Minecraft.getInstance().player;
			return !KeyConflictContext.GUI.isActive() && player != null;
		}

		@Override
		public boolean conflicts(IKeyConflictContext other) {
			return other == this || other == KeyConflictContext.IN_GAME;
		}
	}
}
