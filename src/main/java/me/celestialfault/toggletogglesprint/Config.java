package me.celestialfault.toggletogglesprint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.*;
import java.nio.file.Path;

public class Config {

	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("toggle-toggle-sprint.json");
	private static final TypeAdapter<JsonObject> ADAPTER = new Gson().getAdapter(JsonObject.class);
	public static final Config INSTANCE = new Config();

	public boolean toggleOnUse = true;
	public boolean keepSprintingOnDeath = true;

	public boolean sprintOnJoin = true;
	public ToggleState defaultSprintState = ToggleState.UNCHANGED;

	public boolean sneakOnJoin = false;
	public ToggleState defaultSneakState = ToggleState.UNCHANGED;

	private Config() {
		File configFile = PATH.toFile();
		if(!configFile.exists()) {
			// create a config file with defaults, as we have no guarantee that yacl and/or modmenu will be loaded
			save();
			return;
		}
		try(FileReader reader = new FileReader(configFile)) {
			JsonObject data = ADAPTER.fromJson(reader);
			sprintOnJoin = getBoolean(data, "sprintOnJoin", true);
			defaultSprintState = getState(data, "defaultSprintState");
			sneakOnJoin = getBoolean(data, "sneakOnJoin", false);
			defaultSneakState = getState(data, "defaultSneakState");
			toggleOnUse = getBoolean(data, "toggleOnUse", true);
			keepSprintingOnDeath = getBoolean(data, "keepSprintingOnDeath", true);
		} catch(IOException e) {
			ToggleToggleSprint.LOGGER.error("Failed to load config", e);
		}
	}

	protected void save() {
		File configFile = PATH.toFile();
		try(FileWriter writer = new FileWriter(configFile); JsonWriter jsonWriter = new JsonWriter(writer)) {
			jsonWriter.setIndent("  ");
			JsonObject object = new JsonObject();
			object.addProperty("sprintOnJoin", sprintOnJoin);
			object.addProperty("defaultSprintState", defaultSprintState.name());
			object.addProperty("sneakOnJoin", sneakOnJoin);
			object.addProperty("defaultSneakState", defaultSneakState.name());
			object.addProperty("toggleOnUse", toggleOnUse);
			object.addProperty("keepSprintingOnDeath", keepSprintingOnDeath);
			ADAPTER.write(jsonWriter, object);
		} catch(IOException e) {
			ToggleToggleSprint.LOGGER.error("Failed to save config", e);
		}
	}

	private boolean getBoolean(JsonObject data, String key, boolean defaultValue) {
		return data.has(key) ? data.get(key).getAsBoolean() : defaultValue;
	}

	private ToggleState getState(JsonObject data, String key) {
		try {
			return data.has(key) ? ToggleState.valueOf(data.get(key).getAsString().toUpperCase()) : ToggleState.UNCHANGED;
		} catch(IllegalArgumentException e) {
			ToggleToggleSprint.LOGGER.warn("State {} is invalid; defaulting to UNCHANGED", data.get(key).getAsString());
			return ToggleState.UNCHANGED;
		}
	}

	public Screen getConfigScreen(Screen parent) {
		return YetAnotherConfigLib.createBuilder()
			.title(Text.translatable("toggle-toggle-sprint.name"))
			.category(ConfigCategory.createBuilder()
				.name(Text.translatable("toggle-toggle-sprint.name"))
				.group(buildGeneral())
				.group(buildSprint())
				.group(buildSneak())
				.build())
			.save(this::save)
			.build()
			.generateScreen(parent);
	}

	private OptionGroup buildGeneral() {
		return OptionGroup.createBuilder()
				.name(Text.translatable("toggle-toggle-sprint.general"))
				.option(Option.<Boolean>createBuilder()
						.name(Text.translatable("toggle-toggle-sprint.toggleOnUse"))
						.description(OptionDescription.of(Text.translatable("toggle-toggle-sprint.toggleOnUse.description")))
						.binding(true, () -> toggleOnUse, value -> this.toggleOnUse = value)
						.controller(TickBoxControllerBuilderImpl::new)
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Text.translatable("toggle-toggle-sprint.sprintOnDeath"))
						.description(OptionDescription.of(Text.translatable("toggle-toggle-sprint.sprintOnDeath.description")))
						.binding(true, () -> keepSprintingOnDeath, value -> this.keepSprintingOnDeath = value)
						.controller(TickBoxControllerBuilderImpl::new)
						.build())
				.build();
	}

	private OptionGroup buildSprint() {
		//noinspection UnstableApiUsage
		return OptionGroup.createBuilder()
				.name(Text.translatable("toggle-toggle-sprint.sprint"))
				.option(Option.<Boolean>createBuilder()
						.name(Text.translatable("toggle-toggle-sprint.sprintOnJoin"))
						.description(OptionDescription.of(Text.translatable("toggle-toggle-sprint.sprintOnJoin.description")))
						.binding(true, () -> sprintOnJoin, value -> this.sprintOnJoin = value)
						.controller(TickBoxControllerBuilderImpl::new)
						.build())
				.option(Option.<ToggleState>createBuilder()
						.name(Text.translatable("toggle-toggle-sprint.sprintState"))
						.description(option -> OptionDescription.of(Text.translatable("toggle-toggle-sprint.sprintState.description")
								.append("\n\n")
								.append(Text.translatable("toggle-toggle-sprint.sprintState." + option.name().toLowerCase()))))
						.binding(ToggleState.UNCHANGED, () -> defaultSprintState, value -> this.defaultSprintState = value)
						.customController(option -> EnumControllerBuilder.create(option).enumClass(ToggleState.class).build())
						.build())
				.build();
	}

	private OptionGroup buildSneak() {
		//noinspection UnstableApiUsage
		return OptionGroup.createBuilder()
				.name(Text.translatable("toggle-toggle-sprint.sneak"))
				.option(Option.<Boolean>createBuilder()
						.name(Text.translatable("toggle-toggle-sprint.sneakOnJoin"))
						.description(OptionDescription.of(
								Text.translatable("toggle-toggle-sprint.sneakOnJoin.description")
										.append("\n\n")
										.append(Text.translatable("toggle-toggle-sprint.sneakOnJoin.warning").formatted(Formatting.RED))))
						.binding(false, () -> sneakOnJoin, sneakOnJoin -> this.sneakOnJoin = sneakOnJoin)
						.controller(TickBoxControllerBuilderImpl::new)
						.build())
				.option(Option.<ToggleState>createBuilder()
						.name(Text.translatable("toggle-toggle-sprint.sneakState"))
						.description(option -> OptionDescription.of(Text.translatable("toggle-toggle-sprint.sneakState.description")
								.append("\n\n")
								.append(Text.translatable("toggle-toggle-sprint.sneakState." + option.name().toLowerCase()))))
						.binding(ToggleState.UNCHANGED, () -> defaultSneakState, value -> this.defaultSneakState = value)
						.customController(option -> EnumControllerBuilder.create(option).enumClass(ToggleState.class).build())
						.build())
				.build();
	}

	public enum ToggleState {
		ON, OFF, UNCHANGED
	}
}
