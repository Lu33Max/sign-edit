package de.lumax.signedit.config;

import net.minecraft.network.chat.Component;

public enum AutoLineBreakMode {
    OFF("signedit.config.automatic_line_break.off"),
    JUMP_TO_NEXT_LINE("signedit.config.automatic_line_break.jump"),
    MOVE_WORD_TO_NEXT_LINE("signedit.config.automatic_line_break.move_word");

    private final String translationKey;

    AutoLineBreakMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public AutoLineBreakMode next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
