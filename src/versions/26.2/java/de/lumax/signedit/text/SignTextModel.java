package de.lumax.signedit.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.entity.SignText;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SignTextModel {

    private final StyledLine[] lines = {
            new StyledLine(),
            new StyledLine(),
            new StyledLine(),
            new StyledLine()
    };

    private static TextStyle fromMinecraftStyle(Style style) {
        Integer color = null;

        if (style.getColor() != null) {
            color = style.getColor().getValue();
        }

        return new TextStyle(
                style.isBold(),
                style.isItalic(),
                style.isUnderlined(),
                style.isStrikethrough(),
                style.isObfuscated(),
                color
        );
    }

    public StyledLine getLine(int index) {
        return lines[index];
    }

    public void setLineText(
            int line,
            String text
    ) {
        lines[line].setText(text);
    }

    public void setLineText(
            int index,
            String text,
            int editStart,
            int editEnd,
            TextStyle insertedStyle
    ) {
        lines[index].replaceText(text, editStart, editEnd, insertedStyle);
    }

    public TextStyle getStyleAt(int line, int index) {
        if (line < 0 || line >= lines.length) {
            return TextStyle.EMPTY;
        }

        return lines[line].getStyleAt(index);
    }

    public void moveLineSuffix(
            int sourceLine,
            int startIndex,
            int targetLine
    ) {
        StyledLine source = lines[sourceLine];
        String sourceText = source.getText();
        startIndex = Math.clamp(startIndex, 0, sourceText.length());

        String movedText = sourceText.substring(startIndex);
        List<TextStyle> movedStyles = new ArrayList<>(movedText.length());

        for (int index = startIndex; index < sourceText.length(); index++) {
            movedStyles.add(source.getStyleAt(index));
        }

        source.replaceText(
                sourceText.substring(0, startIndex),
                startIndex,
                sourceText.length()
        );
        lines[targetLine].insertText(0, movedText, movedStyles);
    }

    public void setFormatting(
            int line,
            int start,
            int end,
            FormattingType type,
            boolean value
    ) {
        if (line >= 0 && line < lines.length) {
            lines[line].setFormatting(start, end, type, value);
        }
    }

    public void setColor(
            int line,
            int start,
            int end,
            @Nullable Integer color
    ) {
        if (line < 0 || line >= lines.length) {
            return;
        }

        lines[line].setColor(
                start,
                end,
                color
        );
    }

    public Component buildComponent(int line) {
        String text = lines[line].getText();

        return buildComponent(
                line,
                0,
                text.length()
        );
    }

    public Component buildComponent(
            int line,
            int start,
            int end
    ) {
        StyledLine styledLine = lines[line];

        String text = styledLine.getText();

        start = Math.clamp(start, 0, text.length());
        end = Math.clamp(end, start, text.length());

        if (start == end) {
            return Component.empty();
        }

        Component result = Component.empty();

        int index = start;

        while (index < end) {
            TextStyle style = styledLine.getStyleAt(index);

            int segmentEnd = index + 1;

            while (
                    segmentEnd < end
                            && styledLine.getStyleAt(segmentEnd).equals(style)
            ) {
                segmentEnd++;
            }

            String part = text.substring(index, segmentEnd);

            result = result.copy().append(
                    Component.literal(part)
                            .setStyle(style.toMinecraftStyle())
            );

            index = segmentEnd;
        }

        return result;
    }

    public SignText buildSignText() {
        SignText result = new SignText();

        for (int line = 0; line < 4; line++) {
            result = result.setMessage(
                    line,
                    buildComponent(line),
                    buildComponent(line)
            );
        }

        return result;
    }

    public void loadFromSignText(SignText signText, boolean filtered) {
        for (int line = 0; line < 4; line++) {
            Component component = signText.getMessage(line, filtered);

            StyledLine styledLine = lines[line];

            styledLine.setText(component.getString());
            styledLine.clearStyles();

            int offset = 0;

            for (Component part : component.toFlatList()) {
                String text = part.getString();

                if (text.isEmpty()) {
                    continue;
                }

                Style style = part.getStyle();

                TextStyle ourStyle = fromMinecraftStyle(style);

                if (!ourStyle.equals(TextStyle.EMPTY)) {
                    styledLine.addRange(
                            new StyleRange(
                                    offset,
                                    offset + text.length(),
                                    ourStyle
                            )
                    );
                }

                offset += text.length();
            }
        }
    }

}
