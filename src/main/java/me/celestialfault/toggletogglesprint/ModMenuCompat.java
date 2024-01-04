package me.celestialfault.toggletogglesprint;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class ModMenuCompat implements ModMenuApi {
	private static final String YACL_MODRINTH_LINK = "https://modrinth.com/mod/yacl";

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if(FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
			return Config.INSTANCE::getConfigScreen;
		}
		// the message here is slightly off on <1.19.4 as yacl3 isn't available on such versions, but oh well.
		return (parent) -> new ConfirmLinkScreen((confirmed) -> {
			if(confirmed) {
				Util.getOperatingSystem().open(YACL_MODRINTH_LINK);
			}
			MinecraftClient.getInstance().setScreen(parent);
		}, Text.translatable("toggle-toggle-sprint.noYacl").formatted(Formatting.RED), YACL_MODRINTH_LINK, true);
	}
}
