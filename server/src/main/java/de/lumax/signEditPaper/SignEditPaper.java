package de.lumax.signEditPaper;

import org.bukkit.plugin.java.JavaPlugin;

public final class SignEditPaper extends JavaPlugin {
    public static final String CHANNEL = "signedit:sign_formatting";

    @Override
    public void onEnable() {
        getLogger().info("SignEdit-Paper enabled");

        getServer().getPluginManager().registerEvents(
            new SignChangeListener(this),
            this
        );
        getServer().getMessenger().registerIncomingPluginChannel(
            this,
            CHANNEL,
            new SignFormattingListener(this)
        );
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(
            this,
            CHANNEL
        );
    }
}
