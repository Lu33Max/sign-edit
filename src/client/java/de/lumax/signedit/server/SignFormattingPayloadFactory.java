package de.lumax.signedit.server;

import de.lumax.signedit.network.SignFormattingPayload;
import de.lumax.signedit.text.SignTextModel;
import de.lumax.signedit.text.StyledLine;
import de.lumax.signedit.text.TextStyle;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public final class SignFormattingPayloadFactory {

    private SignFormattingPayloadFactory() {
    }

    public static SignFormattingPayload create(
            BlockPos pos,
            boolean front,
            SignTextModel model
    ) {
        List<SignFormattingPayload.FormattedLine> lines =
                new ArrayList<>();

        for (StyledLine line : model.getLines()) {
            lines.add(createLine(line));
        }

        return new SignFormattingPayload(
                pos,
                front,
                lines
        );
    }

    private static SignFormattingPayload.FormattedLine createLine(
            StyledLine line
    ) {
        String text = line.getText();

        if (text.isEmpty()) {
            return new SignFormattingPayload.FormattedLine(
                    text,
                    List.of()
            );
        }

        List<SignFormattingPayload.FormattedSegment> segments =
                new ArrayList<>();

        int index = 0;

        while (index < text.length()) {
            TextStyle style = line.getStyleAt(index);

            int end = index + 1;

            while (
                    end < text.length()
                            && line.getStyleAt(end).equals(style)
            ) {
                end++;
            }

            segments.add(
                    new SignFormattingPayload.FormattedSegment(
                            text.substring(index, end),
                            style.bold(),
                            style.italic(),
                            style.underlined(),
                            style.strikethrough(),
                            style.obfuscated(),
                            style.color() == null
                                    ? -1
                                    : style.color()
                    )
            );

            index = end;
        }

        return new SignFormattingPayload.FormattedLine(
                text,
                segments
        );
    }
}