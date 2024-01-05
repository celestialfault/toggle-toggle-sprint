package me.celestialfault.toggletogglesprint;

import net.minecraft.client.option.KeyBinding;

public class OnPressKeyBinding extends KeyBinding {

	private final OnPress onPress;

	public OnPressKeyBinding(String translationKey, int code, String category, OnPress onPress) {
		super(translationKey, code, category);
		this.onPress = onPress;
	}

	@Override
	public void setPressed(boolean pressed) {
		if(pressed && !isPressed()) {
			this.onPress.handle();
		}
		super.setPressed(pressed);
	}

	@FunctionalInterface
	public interface OnPress {
		void handle();
	}
}
