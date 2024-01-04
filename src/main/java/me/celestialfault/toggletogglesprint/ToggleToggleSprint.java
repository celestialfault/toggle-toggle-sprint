package me.celestialfault.toggletogglesprint;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;

public class ToggleToggleSprint implements ClientModInitializer {

	public static final Logger LOGGER = LogUtils.getLogger();
	private boolean inWorld = false;

	public static final KeyBinding TOGGLE_SPRINT = new KeyBinding("key.toggle-toggle-sprint.sprint", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.MOVEMENT_CATEGORY) {
		@Override
		public void setPressed(boolean pressed) {
			if(pressed && !isPressed()) {
				MinecraftClient client = MinecraftClient.getInstance();
				toggleOption(client.options.sprintToggled, client.options.sprintKey, Config.INSTANCE.alsoStartSprinting);
			}
			super.setPressed(pressed);
		}
	};

	public static final KeyBinding TOGGLE_SNEAK = new KeyBinding("key.toggle-toggle-sprint.sneak", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.MOVEMENT_CATEGORY) {
		@Override
		public void setPressed(boolean pressed) {
			if(pressed && !isPressed()) {
				MinecraftClient client = MinecraftClient.getInstance();
				toggleOption(client.options.sneakToggled, client.options.sneakKey, Config.INSTANCE.alsoStartSneaking);
			}
			super.setPressed(pressed);
		}
	};

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(TOGGLE_SPRINT);
		KeyBindingHelper.registerKeyBinding(TOGGLE_SNEAK);
		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
	}

	private void onTick(MinecraftClient client) {
		if(client.world == null && inWorld) {
			inWorld = false;
		} else if(client.world != null && !inWorld) {
			doDefaultState(client);
			setKeybindStates(client);
			inWorld = true;
		}
	}

	private void doDefaultState(MinecraftClient client) {
		// Default sprint state
		if(Config.INSTANCE.defaultSprintState == Config.ToggleState.ON) {
			client.options.sprintToggled.setValue(true);
		} else if(Config.INSTANCE.defaultSprintState == Config.ToggleState.OFF) {
			client.options.sprintToggled.setValue(false);
		}

		// Default sneak state
		if(Config.INSTANCE.defaultSneakState == Config.ToggleState.ON) {
			client.options.sneakToggled.setValue(true);
		} else if(Config.INSTANCE.defaultSneakState == Config.ToggleState.OFF) {
			client.options.sneakToggled.setValue(false);
		}
	}

	private void setKeybindStates(MinecraftClient client) {
		if(Config.INSTANCE.sprintOnJoin && client.options.sprintToggled.getValue() && !client.options.sprintKey.isPressed()) {
			client.options.sprintKey.setPressed(true);
		}
		if(Config.INSTANCE.sneakOnJoin && client.options.sneakToggled.getValue() && !client.options.sneakKey.isPressed()) {
			client.options.sneakKey.setPressed(true);
		}
	}

	private static void toggleOption(SimpleOption<Boolean> toggle, KeyBinding keybind, boolean activateKey) {
		toggle.setValue(!toggle.getValue());
		if(activateKey && !keybind.isPressed() || !toggle.getValue()) keybind.setPressed(toggle.getValue());
	}
}
