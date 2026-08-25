package de.lumax.signedit.gui;

import de.lumax.client.mixin.ScreenInvoker;
import de.lumax.signedit.access.SignEditScreenAccess;
import de.lumax.signedit.text.FormattingType;
import de.lumax.signedit.text.TextStyle;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Map;

public final class SignFormattingToolbar {

    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 2;

    private final Map<FormattingType, Button> buttons = new EnumMap<>(FormattingType.class);

    private SignFormattingToolbar() {
    }

    public static SignFormattingToolbar addTo(Screen screen) {
        ScreenInvoker invoker = (ScreenInvoker) screen;
        SignEditScreenAccess access = (SignEditScreenAccess) screen;
        SignFormattingToolbar toolbar = new SignFormattingToolbar();
        int startX = screen.width / 2 - 184;
        int y = screen.height / 4 + 90;

        toolbar.addFormattingButton(invoker, access, "B", FormattingType.BOLD, startX, y);
        toolbar.addFormattingButton(invoker, access, "I", FormattingType.ITALIC, startX + BUTTON_WIDTH + BUTTON_GAP, y);
        toolbar.addFormattingButton(invoker, access, "U", FormattingType.UNDERLINED, startX + 2 * (BUTTON_WIDTH + BUTTON_GAP), y);
        toolbar.addFormattingButton(invoker, access, "S", FormattingType.STRIKETHROUGH, startX + 3 * (BUTTON_WIDTH + BUTTON_GAP), y);
        toolbar.addFormattingButton(invoker, access, "O", FormattingType.OBFUSCATED, startX + 4 * (BUTTON_WIDTH + BUTTON_GAP), y);
        toolbar.update(access.signedit$getActiveStyle());
        return toolbar;
    }

    public void update(TextStyle style) {
        for (Map.Entry<FormattingType, Button> entry : buttons.entrySet()) {
            boolean enabled = isEnabled(style, entry.getKey());
            String label = switch (entry.getKey()) {
                case BOLD -> "B";
                case ITALIC -> "I";
                case UNDERLINED -> "U";
                case STRIKETHROUGH -> "S";
                case OBFUSCATED -> "O";
            };
            entry.getValue().setMessage(Component.literal(enabled ? "[" + label + "]" : label));
        }
    }

    private void addFormattingButton(ScreenInvoker invoker, SignEditScreenAccess access, String label, FormattingType type, int x, int y) {
        Button button = Button.builder(Component.literal(label), ignored -> {
                    access.signedit$toggleFormatting(type);
                    access.signedit$clearToolbarFocusAfterClick();
                })
                .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        buttons.put(type, button);
        invoker.signedit$addRenderableWidget(button);
    }

    private static boolean isEnabled(TextStyle style, FormattingType type) {
        return switch (type) {
            case BOLD -> style.bold();
            case ITALIC -> style.italic();
            case UNDERLINED -> style.underlined();
            case STRIKETHROUGH -> style.strikethrough();
            case OBFUSCATED -> style.obfuscated();
        };
    }
}
