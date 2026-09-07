package de.lumax.signEditPaper;

import io.papermc.paper.registry.RegistryAccess;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public final class SignFormattingListener
        implements PluginMessageListener {

    private final SignEditPaper plugin;

    public SignFormattingListener(SignEditPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            @NotNull byte[] message
    ) {
        if (!SignEditPaper.CHANNEL.equals(channel)) {
            return;
        }

        try {
            SignFormattingPayload payload =
                    SignFormattingDecoder.decode(message);

            plugin.getLogger().info(
                    "Decoded payload: pos="
                            + payload.pos()
                            + ", front="
                            + payload.front()
                            + ", lines="
                            + payload.lines().size()
            );

            for (int i = 0; i < payload.lines().size(); i++) {
                SignFormattingPayload.FormattedLine line =
                        payload.lines().get(i);

                plugin.getLogger().info(
                        "Line " + i
                                + ": text=\"" + line.text()
                                + "\", segments="
                                + line.segments().size()
                );

                for (SignFormattingPayload.FormattedSegment segment : line.segments()) {
                    plugin.getLogger().info(
                            "  segment=\""
                                    + segment.text()
                                    + "\""
                                    + " bold=" + segment.bold()
                                    + " italic=" + segment.italic()
                                    + " underline=" + segment.underlined()
                                    + " strike=" + segment.strikethrough()
                                    + " obfuscated=" + segment.obfuscated()
                                    + " color=0x"
                                    + Integer.toHexString(segment.color())
                    );
                }
            }

            org.bukkit.block.Block block =
                    player.getWorld().getBlockAt(
                            payload.pos().getX(),
                            payload.pos().getY(),
                            payload.pos().getZ()
                    );

            if (!(block.getState() instanceof org.bukkit.block.Sign sign)) {
                plugin.getLogger().warning(
                        "Payload position is not a sign: " + payload.pos()
                );
                return;
            }

            PendingSignFormatting.tryApply(
                    player.getUniqueId(),
                    payload,
                    plugin
            );

        } catch (Exception e) {
            plugin.getLogger().warning(
                    "Failed to decode SignEdit payload: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private Component createSegment(
            SignFormattingPayload.FormattedSegment segment
    ) {
        Component component = Component.text(segment.text());

        if (segment.color() != 0xFFFFFFFF) {
            component = component.color(
                    TextColor.color(segment.color() & 0xFFFFFF)
            );
        }

        if (segment.bold()) {
            component = component.decorate(TextDecoration.BOLD);
        }

        if (segment.italic()) {
            component = component.decorate(TextDecoration.ITALIC);
        }

        if (segment.underlined()) {
            component = component.decorate(TextDecoration.UNDERLINED);
        }

        if (segment.strikethrough()) {
            component = component.decorate(TextDecoration.STRIKETHROUGH);
        }

        if (segment.obfuscated()) {
            component = component.decorate(TextDecoration.OBFUSCATED);
        }

        return component;
    }

    private Component createLine(
            SignFormattingPayload.FormattedLine line
    ) {
        Component result = Component.empty();

        for (SignFormattingPayload.FormattedSegment segment : line.segments()) {
            result = result.append(createSegment(segment));
        }

        return result;
    }

    private void applyFormatting(
            Sign sign,
            SignFormattingPayload payload
    ) {
        SignSide side = sign.getSide(
                payload.front() ? Side.FRONT : Side.BACK
        );

        for (int i = 0; i < 4; i++) {
            SignFormattingPayload.FormattedLine line =
                    payload.lines().get(i);

            side.line(i, createLine(line));
        }

        if (!sign.update(true, false)) {
            plugin.getLogger().warning(
                    "Failed to update sign at " + payload.pos()
            );
        }

        plugin.getLogger().info(
                "[SignEdit] formatting applied to "
                        + payload.pos()
        );


    }
}
