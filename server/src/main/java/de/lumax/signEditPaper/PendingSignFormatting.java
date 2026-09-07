package de.lumax.signEditPaper;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PendingSignFormatting {

    private PendingSignFormatting() {
    }

    private record Key(
            UUID playerId,
            net.minecraft.core.BlockPos pos,
            boolean front
    ) {
    }

    private static final class Pending {
        SignFormattingPayload payload;
        boolean vanillaObserved;
    }

    private static final Map<Key, Pending> PENDING = new HashMap<>();

    public static void submitPayload(
            UUID playerId,
            SignFormattingPayload payload
    ) {
        Key key = new Key(
                playerId,
                payload.pos(),
                payload.front()
        );

        Pending pending = PENDING.computeIfAbsent(
                key,
                ignored -> new Pending()
        );

        pending.payload = payload;
    }

    public static void vanillaUpdateObserved(
            UUID playerId,
            net.minecraft.core.BlockPos pos,
            boolean front,
            JavaPlugin plugin
    ) {
        Key key = new Key(playerId, pos, front);

        Pending pending = PENDING.computeIfAbsent(
                key,
                ignored -> new Pending()
        );

        pending.vanillaObserved = true;

        scheduleApply(plugin, key);
    }

    public static void tryApply(
            UUID playerId,
            SignFormattingPayload payload,
            JavaPlugin plugin
    ) {
        Key key = new Key(
                playerId,
                payload.pos(),
                payload.front()
        );

        Pending pending = PENDING.computeIfAbsent(
                key,
                ignored -> new Pending()
        );

        pending.payload = payload;

        if (pending.vanillaObserved) {
            scheduleApply(plugin, key);
        }
    }

    private static void scheduleApply(
            JavaPlugin plugin,
            Key key
    ) {
        JavaPlugin.getProvidingPlugin(PendingSignFormatting.class)
                .getServer()
                .getScheduler()
                .runTask(
                        plugin,
                        () -> apply(plugin, key)
                );
    }

    private static void apply(
            JavaPlugin plugin,
            Key key
    ) {
        Pending pending = PENDING.get(key);

        if (pending == null) {
            return;
        }

        if (!pending.vanillaObserved || pending.payload == null) {
            return;
        }

        SignFormattingServerPaper.apply(
                plugin,
                key.playerId(),
                pending.payload
        );

        PENDING.remove(key);
    }
}
