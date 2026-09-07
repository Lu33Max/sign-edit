package de.lumax.signEditPaper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class SignFormattingServerPaper {

    private SignFormattingServerPaper() {
    }

    public static void apply(
            JavaPlugin plugin,
            UUID playerId,
            SignFormattingPayload payload
    ) {
        Player player = Bukkit.getPlayer(playerId);

        if (player == null) {
            return;
        }

        Block block = player.getWorld().getBlockAt(
                payload.pos().getX(),
                payload.pos().getY(),
                payload.pos().getZ()
        );

        if (!(block.getState() instanceof Sign sign)) {
            plugin.getLogger().warning(
                    "Payload position is not a sign: "
                            + payload.pos()
            );
            return;
        }

        SignSide side = sign.getSide(
                payload.front()
                        ? Side.FRONT
                        : Side.BACK
        );

        for (int i = 0; i < 4; i++) {
            side.line(
                    i,
                    createLine(payload.lines().get(i))
            );
        }

        if (!sign.update(true, false)) {
            plugin.getLogger().warning(
                    "Failed to update sign at "
                            + payload.pos()
            );
        }

        plugin.getLogger().info(
                "[SignEdit] formatting applied after vanilla update: "
                        + payload.pos()
        );
    }

    private static Component createLine(
            SignFormattingPayload.FormattedLine line
    ) {
        Component result = Component.empty();

        for (SignFormattingPayload.FormattedSegment segment : line.segments()) {
            result = result.append(createSegment(segment));
        }

        return result;
    }

    private static Component createSegment(
            SignFormattingPayload.FormattedSegment segment
    ) {
        Component component =
                Component.text(segment.text());

        if (segment.color() != 0xFFFFFFFF) {
            component = component.color(
                    TextColor.color(
                            segment.color() & 0xFFFFFF
                    )
            );
        }

        if (segment.bold()) {
            component = component.decorate(
                    TextDecoration.BOLD
            );
        }

        if (segment.italic()) {
            component = component.decorate(
                    TextDecoration.ITALIC
            );
        }

        if (segment.underlined()) {
            component = component.decorate(
                    TextDecoration.UNDERLINED
            );
        }

        if (segment.strikethrough()) {
            component = component.decorate(
                    TextDecoration.STRIKETHROUGH
            );
        }

        if (segment.obfuscated()) {
            component = component.decorate(
                    TextDecoration.OBFUSCATED
            );
        }

        return component;
    }
}
