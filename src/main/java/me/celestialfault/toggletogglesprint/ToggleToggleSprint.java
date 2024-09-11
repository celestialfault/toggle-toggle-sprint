package me.celestialfault.toggletogglesprint;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public class ToggleToggleSprint implements ClientModInitializer {

	public static final Logger LOGGER = LogUtils.getLogger();
	private boolean inWorld = false;

	public static final OnPressKeyBinding TOGGLE_SPRINT = new OnPressKeyBinding("key.toggle-toggle-sprint.sprint", GLFW.GLFW_KEY_RIGHT_CONTROL, KeyBinding.MOVEMENT_CATEGORY, () -> {
		MinecraftClient client = MinecraftClient.getInstance();
		toggleOption(client.options.getSprintToggled(), client.options.sprintKey, Config.INSTANCE.alsoStartSprinting);
	});

	public static final OnPressKeyBinding TOGGLE_SNEAK = new OnPressKeyBinding("key.toggle-toggle-sprint.sneak", GLFW.GLFW_KEY_RIGHT_SHIFT, KeyBinding.MOVEMENT_CATEGORY, () -> {
		MinecraftClient client = MinecraftClient.getInstance();
		toggleOption(client.options.getSneakToggled(), client.options.sneakKey, Config.INSTANCE.alsoStartSneaking);
	});

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
			applyJoinStates(client);
			inWorld = true;
		}
	}

	private void applyJoinStates(MinecraftClient client) {
		// Default sprint state
		if(Config.INSTANCE.defaultSprintState == Config.ToggleState.ON) {
			client.options.getSprintToggled().setValue(true);
			// pressing the key is done later
		} else if(Config.INSTANCE.defaultSprintState == Config.ToggleState.OFF) {
			client.options.getSprintToggled().setValue(false);
			// ... but, we still want to ensure that the key is unpressed if we're loading into a world for a
			// second time, and the sprint key was toggled when the last one was left
			client.options.sprintKey.setPressed(false);
		}

		// Default sneak state
		if(Config.INSTANCE.defaultSneakState == Config.ToggleState.ON) {
			client.options.getSneakToggled().setValue(true);
		} else if(Config.INSTANCE.defaultSneakState == Config.ToggleState.OFF) {
			client.options.getSneakToggled().setValue(false);
			client.options.sneakKey.setPressed(false);
		}

		// Press the keys if they're configured to be pressed when joining a world, and the relevant toggle latch is on
		if(Config.INSTANCE.sprintOnJoin && client.options.getSprintToggled().getValue() && !client.options.sprintKey.isPressed()) {
			client.options.sprintKey.setPressed(true);
		}
		if(Config.INSTANCE.sneakOnJoin && client.options.getSneakToggled().getValue() && !client.options.sneakKey.isPressed()) {
			client.options.sneakKey.setPressed(true);
		}
	}

	private static void toggleOption(SimpleOption<Boolean> toggle, KeyBinding keybind, boolean activateKey) {
		if(toggle.getValue() && activateKey && !keybind.isPressed()) {
			// if the toggle latch is already enabled but the key isn't pressed, and we're configured to simulate
			// the key press, then just simulate the key press instead of turning off the latch to avoid requiring
			// pressing the toggle key twice to start sprinting/sneaking.
			keybind.setPressed(true);
			return;
		}
		toggle.setValue(!toggle.getValue());
		if(!toggle.getValue()) {
			long handle = MinecraftClient.getInstance().getWindow().getHandle();
			boolean manuallyHeld = InputUtil.isKeyPressed(handle, KeyBindingHelper.getBoundKeyOf(keybind).getCode());
			// note that we always call this with the value of manuallyHeld in order to handle the case where the player
			// presses the vanilla key, thereby toggling the key's held state off, and then pressing our toggle key,
			// turning off the toggle latch; in such a case, the game wouldn't think the key is being pressed, when
			// it should logically be pressed.
			keybind.setPressed(manuallyHeld);
		} else if(activateKey && !keybind.isPressed()) {
			keybind.setPressed(true);
		}
	}
}
