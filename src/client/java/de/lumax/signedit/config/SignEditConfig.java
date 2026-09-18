package de.lumax.signedit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.lumax.signedit.SignEditClient;
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

    public static final boolean DEFAULT_WRAP_TO_FIRST_LINE = false;

    private static AutoLineBreakMode autoLineBreakMode = AutoLineBreakMode.DEFAULT;
    private static boolean wrapToFirstLine = DEFAULT_WRAP_TO_FIRST_LINE;

    private SignEditConfig() {
    }

    public static AutoLineBreakMode getAutoLineBreakMode() {
        return autoLineBreakMode;
    }

    public static void setAutoLineBreakMode(AutoLineBreakMode mode) {
        autoLineBreakMode = mode;
        save();
    }

    public static boolean isWrapToFirstLineEnabled() {
        return wrapToFirstLine;
    }

    public static void setWrapToFirstLineEnabled(boolean enabled) {
        wrapToFirstLine = enabled;
        save();
    }

    public static void load() {
        if (!Files.exists(PATH)) {
            return;
        }

        try (var reader = Files.newBufferedReader(PATH)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);

            if (data != null) {
                if (data.autoLineBreakMode != null) {
                    autoLineBreakMode = data.autoLineBreakMode;
                }

                wrapToFirstLine = data.wrapToFirstLine;
            }
        } catch (IOException | com.google.gson.JsonParseException exception) {
            LOGGER.error("Failed to load Sign Edit configuration from {}", PATH, exception);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(PATH.getParent());

            try (var writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(new ConfigData(autoLineBreakMode, wrapToFirstLine), writer);
            }
        } catch (IOException exception) {
            LOGGER.error("Failed to save Sign Edit configuration to {}", PATH, exception);
        }
    }

    private record ConfigData(AutoLineBreakMode autoLineBreakMode, boolean wrapToFirstLine) {
    }
}
