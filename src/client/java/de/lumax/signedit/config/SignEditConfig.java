package de.lumax.signedit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.lumax.client.SignEditClient;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SignEditConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LoggerFactory.getLogger(SignEditClient.MOD_ID);
    private static final Path PATH = Minecraft.getInstance()
            .gameDirectory
            .toPath()
            .resolve("config")
            .resolve("signedit.json");

    private static AutoLineBreakMode autoLineBreakMode = AutoLineBreakMode.OFF;

    private SignEditConfig() {
    }

    public static AutoLineBreakMode getAutoLineBreakMode() {
        return autoLineBreakMode;
    }

    public static void setAutoLineBreakMode(AutoLineBreakMode mode) {
        autoLineBreakMode = mode;
        save();
    }

    public static void load() {
        if (!Files.exists(PATH)) {
            return;
        }

        try (var reader = Files.newBufferedReader(PATH)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);

            if (data != null && data.autoLineBreakMode != null) {
                autoLineBreakMode = data.autoLineBreakMode;
            }
        } catch (IOException | com.google.gson.JsonParseException exception) {
            LOGGER.error("Failed to load Sign Edit configuration from {}", PATH, exception);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(PATH.getParent());

            try (var writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(new ConfigData(autoLineBreakMode), writer);
            }
        } catch (IOException exception) {
            LOGGER.error("Failed to save Sign Edit configuration to {}", PATH, exception);
        }
    }

    private record ConfigData(AutoLineBreakMode autoLineBreakMode) {
    }
}
