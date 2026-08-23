package de.lumax.signedit.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public record SignFormattingPayload(
        BlockPos pos,
        boolean front,
        List<FormattedLine> lines
) implements CustomPacketPayload {

    public static final Identifier ID =
            Identifier.fromNamespaceAndPath("signedit", "sign_formatting");

    public static final Type<SignFormattingPayload> TYPE =
            new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SignFormattingPayload> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    SignFormattingPayload::pos,

                    ByteBufCodecs.BOOL,
                    SignFormattingPayload::front,

                    ByteBufCodecs.collection(
                            ArrayList::new,
                            FormattedLine.CODEC
                    ),
                    SignFormattingPayload::lines,

                    SignFormattingPayload::new
            );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record FormattedLine(
            String text,
            List<FormattedSegment> segments
    ) {

        public static final StreamCodec<RegistryFriendlyByteBuf, FormattedLine> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8,
                        FormattedLine::text,

                        ByteBufCodecs.collection(
                                ArrayList::new,
                                FormattedSegment.CODEC
                        ),
                        FormattedLine::segments,

                        FormattedLine::new
                );
    }

    public record FormattedSegment(
            String text,
            boolean bold,
            boolean italic,
            boolean underlined,
            boolean strikethrough,
            boolean obfuscated,
            int color
    ) {

        public static final StreamCodec<RegistryFriendlyByteBuf, FormattedSegment> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8,
                        FormattedSegment::text,

                        ByteBufCodecs.BOOL,
                        FormattedSegment::bold,

                        ByteBufCodecs.BOOL,
                        FormattedSegment::italic,

                        ByteBufCodecs.BOOL,
                        FormattedSegment::underlined,

                        ByteBufCodecs.BOOL,
                        FormattedSegment::strikethrough,

                        ByteBufCodecs.BOOL,
                        FormattedSegment::obfuscated,

                        ByteBufCodecs.INT,
                        FormattedSegment::color,

                        FormattedSegment::new
                );
    }
}