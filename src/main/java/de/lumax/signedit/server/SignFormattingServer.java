package de.lumax.signedit.server;

import de.lumax.signedit.network.SignFormattingPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

public final class SignFormattingServer {

    private SignFormattingServer() {
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                SignFormattingPayload.TYPE,
                (payload, context) -> {

                    ServerPlayer player = context.player();

                    System.out.println(
                            "[SignEdit] formatting payload received: "
                                    + payload.pos()
                                    + " front="
                                    + payload.front()
                    );

                    SignFormattingPayload immediatelyApply =
                            PendingSignFormatting.submit(
                                    player.getUUID(),
                                    payload
                            );

                    System.out.println(
                            "[SignEdit] payload submit -> immediate apply: "
                                    + (immediatelyApply != null)
                    );

                    if (immediatelyApply != null) {
                        SignFormattingServer.applyFromMixin(
                                player,
                                immediatelyApply
                        );
                    }
                }
        );
    }

    private static SignBlockEntity getValidSign(
            ServerPlayer player,
            net.minecraft.core.BlockPos pos
    ) {
        if (!player.level().hasChunkAt(pos)) {
            return null;
        }

        BlockEntity blockEntity =
                player.level().getBlockEntity(pos);

        if (!(blockEntity instanceof SignBlockEntity sign)) {
            return null;
        }

        if (sign.isWaxed()) {
            return null;
        }

        if (player.distanceToSqr(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        ) > 64.0) {
            return null;
        }

        return sign;
    }

    private static boolean validatePayload(
            SignFormattingPayload payload
    ) {
        if (payload.lines().size() != 4) {
            return false;
        }

        for (SignFormattingPayload.FormattedLine line : payload.lines()) {
            if (line.text().length() > 384) {
                return false;
            }

            int length = 0;

            for (SignFormattingPayload.FormattedSegment segment : line.segments()) {
                if (segment.text().isEmpty()) {
                    return false;
                }

                if (segment.color() < -1
                        || segment.color() > 0xFFFFFF) {
                    return false;
                }

                length += segment.text().length();
            }

            if (length != line.text().length()) {
                return false;
            }
        }

        return true;
    }

    public static void applyFromMixin(
            ServerPlayer player,
            SignFormattingPayload payload
    ) {
        applyFormatting(player, payload);
    }

    private static void applyFormatting(
            ServerPlayer player,
            SignFormattingPayload payload
    ) {
        if (!player.level().hasChunkAt(payload.pos())) {
            return;
        }

        BlockEntity blockEntity =
                player.level().getBlockEntity(payload.pos());

        if (!(blockEntity instanceof SignBlockEntity sign)) {
            return;
        }

        if (sign.isWaxed()) {
            return;
        }

        if (player.distanceToSqr(
                payload.pos().getX() + 0.5,
                payload.pos().getY() + 0.5,
                payload.pos().getZ() + 0.5
        ) > 64.0) {
            return;
        }

        SignText signText = sign.getText(payload.front());

        for (int i = 0; i < 4; i++) {
            SignFormattingPayload.FormattedLine line =
                    payload.lines().get(i);

            Component formattedComponent =
                    buildComponent(line);

            signText = signText.setMessage(
                    i,
                    formattedComponent,
                    formattedComponent
            );
        }

        sign.setText(
                signText,
                payload.front()
        );

        player.level().sendBlockUpdated(
                payload.pos(),
                sign.getBlockState(),
                sign.getBlockState(),
                3
        );
    }

    private static Component buildComponent(
            SignFormattingPayload.FormattedLine line
    ) {
        Component result = Component.empty();

        for (SignFormattingPayload.FormattedSegment segment
                : line.segments()) {

            Style style = Style.EMPTY
                    .withBold(segment.bold())
                    .withItalic(segment.italic())
                    .withUnderlined(segment.underlined())
                    .withStrikethrough(segment.strikethrough())
                    .withObfuscated(segment.obfuscated());

            if (segment.color() >= 0) {
                style = style.withColor(
                        TextColor.fromRgb(segment.color())
                );
            }

            result = result.copy().append(
                    Component.literal(segment.text())
                            .setStyle(style)
            );
        }

        return result;
    }
}