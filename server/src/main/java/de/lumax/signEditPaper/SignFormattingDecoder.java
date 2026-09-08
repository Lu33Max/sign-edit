package de.lumax.signEditPaper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;

public final class SignFormattingDecoder {

    private SignFormattingDecoder() {
    }

    private static final StreamCodec<ByteBuf, SignFormattingPayload.FormattedSegment> SEGMENT_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SignFormattingPayload.FormattedSegment::text,

            ByteBufCodecs.BOOL,
            SignFormattingPayload.FormattedSegment::bold,

            ByteBufCodecs.BOOL,
            SignFormattingPayload.FormattedSegment::italic,

            ByteBufCodecs.BOOL,
            SignFormattingPayload.FormattedSegment::underlined,

            ByteBufCodecs.BOOL,
            SignFormattingPayload.FormattedSegment::strikethrough,

            ByteBufCodecs.BOOL,
            SignFormattingPayload.FormattedSegment::obfuscated,

            ByteBufCodecs.INT,
            SignFormattingPayload.FormattedSegment::color,

            SignFormattingPayload.FormattedSegment::new
        );

    private static final StreamCodec<ByteBuf, SignFormattingPayload.FormattedLine> LINE_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SignFormattingPayload.FormattedLine::text,

            ByteBufCodecs.collection(
                ArrayList::new,
                SEGMENT_CODEC
            ),
            SignFormattingPayload.FormattedLine::segments,

            SignFormattingPayload.FormattedLine::new
        );

    private static final StreamCodec<ByteBuf, SignFormattingPayload> PAYLOAD_CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SignFormattingPayload::pos,

            ByteBufCodecs.BOOL,
            SignFormattingPayload::front,

            ByteBufCodecs.collection(
                    ArrayList::new,
                    LINE_CODEC
            ),
            SignFormattingPayload::lines,

            SignFormattingPayload::new
        );

    public static SignFormattingPayload decode(byte[] data) {
        ByteBuf buf = Unpooled.wrappedBuffer(data);

        try {
            return PAYLOAD_CODEC.decode(buf);
        } finally {
            buf.release();
        }
    }
}