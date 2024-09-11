package me.celestialfault.toggletogglesprint.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.celestialfault.toggletogglesprint.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin class to cancel untoggling sprint in an actually sensible way, unlike nearly every other mod that does this on Modrinth.<br>
 * (no, patrick, using an {@code @Inject} as an {@code @Overwrite} for {@code ClientPlayerEntity#requestRespawn()} is not sensible.)
 */
@Mixin(KeyBinding.class)
abstract class KeyBindingMixin {
	//? if >=1.20.1 {
	@WrapWithCondition(method = "untoggleStickyKeys", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/StickyKeyBinding;untoggle()V"))
	private static boolean keepSprintOnDeath(StickyKeyBinding instance) {
		// Only cancel untoggling the sprint key, but don't modify this behavior for other keys
		return instance != MinecraftClient.getInstance().options.sprintKey || !Config.INSTANCE.keepSprintingOnDeath;
	}
	//? }
}
