package de.lumax.signEditPaper;


import net.minecraft.core.BlockPos;

import java.util.List;

public record SignFormattingPayload(
        BlockPos pos,
        boolean front,
        List<FormattedLine> lines
) {

    public record FormattedLine(
            String text,
            List<FormattedSegment> segments
    ) {
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
    }
}
