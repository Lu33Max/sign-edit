package de.lumax.signedit.text;

import net.minecraft.network.chat.Style;

public record TextStyle(
        boolean bold,
        boolean italic,
        boolean underlined,
        boolean strikethrough,
        boolean obfuscated,
        Integer color
) {

    public static final TextStyle EMPTY =
            new TextStyle(
                    false,
                    false,
                    false,
                    false,
                    false,
                    null
            );

    public TextStyle withFormatting(
            FormattingType type,
            boolean value
    ) {
        return switch (type) {
            case BOLD -> withBold(value);
            case ITALIC -> withItalic(value);
            case UNDERLINED -> withUnderlined(value);
            case STRIKETHROUGH -> withStrikethrough(value);
            case OBFUSCATED -> withObfuscated(value);
        };
    }

    public TextStyle withBold(boolean value) {
        return new TextStyle(
                value,
                italic,
                underlined,
                strikethrough,
                obfuscated,
                color
        );
    }

    public TextStyle withItalic(boolean value) {
        return new TextStyle(
                bold,
                value,
                underlined,
                strikethrough,
                obfuscated,
                color
        );
    }

    public TextStyle withUnderlined(boolean value) {
        return new TextStyle(
                bold,
                italic,
                value,
                strikethrough,
                obfuscated,
                color
        );
    }

    public TextStyle withStrikethrough(boolean value) {
        return new TextStyle(
                bold,
                italic,
                underlined,
                value,
                obfuscated,
                color
        );
    }

    public TextStyle withObfuscated(boolean value) {
        return new TextStyle(
                bold,
                italic,
                underlined,
                strikethrough,
                value,
                color
        );
    }

    public TextStyle withColor(Integer rgb) {
        return new TextStyle(
                bold,
                italic,
                underlined,
                strikethrough,
                obfuscated,
                rgb
        );
    }

    public Style toMinecraftStyle() {
        Style style = Style.EMPTY
                .withBold(bold)
                .withItalic(italic)
                .withUnderlined(underlined)
                .withStrikethrough(strikethrough)
                .withObfuscated(obfuscated);

        if (color != null) {
            style = style.withColor(color);
        }

        return style;
    }
}
