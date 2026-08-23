package de.lumax.signedit.gui;

import de.lumax.client.mixin.ScreenInvoker;
import de.lumax.signedit.access.SignEditScreenAccess;
import de.lumax.signedit.text.FormattingType;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SignFormattingToolbar {

    private static final int BUTTON_WIDTH = 30;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 2;

    private SignFormattingToolbar() {
    }

    public static void addTo(Screen screen) {
        ScreenInvoker invoker = (ScreenInvoker) screen;
        SignEditScreenAccess access = (SignEditScreenAccess) screen;

        int buttonCount = 5;

        int totalWidth =
                buttonCount * BUTTON_WIDTH
                        + (buttonCount - 1) * BUTTON_GAP;

        int startX = screen.width / 2 - totalWidth / 2;
        int y = screen.height / 4 + 120;

        addFormattingButton(
                invoker,
                access,
                "B",
                FormattingType.BOLD,
                startX,
                y
        );

        addFormattingButton(
                invoker,
                access,
                "I",
                FormattingType.ITALIC,
                startX + 32,
                y
        );

        addFormattingButton(
                invoker,
                access,
                "U",
                FormattingType.UNDERLINED,
                startX + 64,
                y
        );

        addFormattingButton(
                invoker,
                access,
                "S",
                FormattingType.STRIKETHROUGH,
                startX + 96,
                y
        );

        addFormattingButton(
                invoker,
                access,
                "O",
                FormattingType.OBFUSCATED,
                startX + 128,
                y
        );
    }

    private static void addFormattingButton(
            ScreenInvoker invoker,
            SignEditScreenAccess access,
            String label,
            FormattingType type,
            int x,
            int y
    ) {
        invoker.signedit$addRenderableWidget(
                Button.builder(
                                Component.literal(label),
                                button -> applyFormatting(
                                        access,
                                        type
                                )
                        )
                        .bounds(
                                x,
                                y,
                                BUTTON_WIDTH,
                                BUTTON_HEIGHT
                        )
                        .build()
        );
    }

    private static void applyFormatting(
            SignEditScreenAccess access,
            FormattingType type
    ) {
        if (!access.signedit$isSelecting()) {
            return;
        }

        int cursor = access.signedit$getCursorPos();
        int selection = access.signedit$getSelectionPos();

        int start = Math.min(cursor, selection);
        int end = Math.max(cursor, selection);

        int line = access.signedit$getCurrentLine();

        access.signedit$getModel().toggleFormatting(
                line,
                start,
                end,
                type
        );
    }
}