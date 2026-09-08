package de.lumax.signEditPaper;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

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
            byte @NonNull [] message
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

            Block block =
                player.getWorld().getBlockAt(
                    payload.pos().getX(),
                    payload.pos().getY(),
                    payload.pos().getZ()
                );

            if (!(block.getState() instanceof Sign)) {
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
}
