package me.celestialfault.toggletogglesprint.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.celestialfault.toggletogglesprint.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
	@WrapWithCondition(method = "untoggleStickyKeys", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/StickyKeyBinding;untoggle()V"))
	private static boolean keepSprintOnDeath(StickyKeyBinding instance) {
		// Only cancel untoggling the sprint key
		if(instance == MinecraftClient.getInstance().options.sprintKey) {
			return !Config.INSTANCE.keepSprintingOnDeath;
		}
		// But allow untoggling other keys like the sneak key
		return true;
	}
}
