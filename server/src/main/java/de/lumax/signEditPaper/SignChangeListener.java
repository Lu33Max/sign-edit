package de.lumax.signEditPaper;

import io.papermc.paper.event.packet.UncheckedSignChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignChangeListener implements Listener {

    private final JavaPlugin plugin;

    public SignChangeListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChange(UncheckedSignChangeEvent event) {

        if (event.getPlayer() == null) {
            return;
        }

        net.minecraft.core.BlockPos pos =
                new net.minecraft.core.BlockPos(
                        event.getEditedBlockPosition().blockX(),
                        event.getEditedBlockPosition().blockY(),
                        event.getEditedBlockPosition().blockZ()
                );

        boolean front =
                event.getSide() == org.bukkit.block.sign.Side.FRONT;

        plugin.getLogger().info(
                "[SignEdit] vanilla update observed: "
                        + pos
                        + " front="
                        + front
        );

        PendingSignFormatting.vanillaUpdateObserved(
                event.getPlayer().getUniqueId(),
                pos,
                front,
                plugin
        );
    }
}
