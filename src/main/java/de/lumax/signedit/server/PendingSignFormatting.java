package de.lumax.signedit.server;

import de.lumax.signedit.network.SignFormattingPayload;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PendingSignFormatting {

    private static final long TIMEOUT_MS = 5_000L;

    private static final Map<Key, Entry> ENTRIES = new HashMap<>();

    private PendingSignFormatting() {
    }

    public static synchronized SignFormattingPayload submit(
            UUID playerId,
            SignFormattingPayload payload
    ) {
        cleanup();

        Key key = new Key(
                playerId,
                payload.pos(),
                payload.front()
        );

        Entry old = ENTRIES.get(key);

        System.out.println(
                "[SignEdit] submit: key=" + key
                        + " old=" + (old != null)
                        + " oldVanilla="
                        + (old != null && old.vanillaUpdateObserved)
        );

        if (old != null && old.vanillaUpdateObserved) {
            ENTRIES.remove(key);

            System.out.println(
                    "[SignEdit] vanilla update was already observed -> applying now"
            );

            return payload;
        }

        ENTRIES.put(
                key,
                new Entry(payload, old != null && old.vanillaUpdateObserved)
        );

        System.out.println(
                "[SignEdit] payload stored waiting for vanilla update"
        );

        return null;
    }

    public static synchronized SignFormattingPayload vanillaUpdateObserved(
            UUID playerId,
            BlockPos pos,
            boolean front
    ) {
        cleanup();

        Key key = new Key(
                playerId,
                pos,
                front
        );

        Entry old = ENTRIES.get(key);

        System.out.println(
                "[SignEdit] vanillaUpdateObserved: key=" + key
                        + " old=" + (old != null)
                        + " oldPayload="
                        + (old != null && old.payload != null)
        );

        if (old == null) {
            ENTRIES.put(
                    key,
                    new Entry(null, true)
            );

            System.out.println(
                    "[SignEdit] vanilla update stored, waiting for payload"
            );

            return null;
        }

        if (old.payload != null) {
            ENTRIES.remove(key);

            System.out.println(
                    "[SignEdit] payload already present -> applying now"
            );

            return old.payload;
        }

        ENTRIES.put(
                key,
                new Entry(null, true)
        );

        return null;
    }

    private static void cleanup() {
        long now = System.currentTimeMillis();

        ENTRIES.entrySet().removeIf(
                entry -> now - entry.getValue().createdAt > TIMEOUT_MS
        );
    }

    private record Key(
            UUID playerId,
            BlockPos pos,
            boolean front
    ) {
    }

    private static final class Entry {

        private final SignFormattingPayload payload;
        private final boolean vanillaUpdateObserved;
        private final long createdAt;

        private Entry(
                SignFormattingPayload payload,
                boolean vanillaUpdateObserved
        ) {
            this.payload = payload;
            this.vanillaUpdateObserved = vanillaUpdateObserved;
            this.createdAt = System.currentTimeMillis();
        }
    }
}