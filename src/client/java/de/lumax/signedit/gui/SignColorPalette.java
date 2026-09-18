package de.lumax.signedit.gui;

import de.lumax.signedit.mixin.ScreenInvoker;
import de.lumax.signedit.access.SignEditScreenAccess;
import de.lumax.signedit.text.MinecraftColors;
import net.minecraft.client.gui.screens.Screen;

public final class SignColorPalette {

    private static final int CELL_SIZE = 12;
    private static final int CELL_GAP = 2;

    private SignColorPalette() {
    }

    public static void addTo(
            Screen screen,
            SignEditScreenAccess access,
            int startX,
            int startY
    ) {
        ScreenInvoker invoker =
                (ScreenInvoker) screen;

        for (int i = 0; i < MinecraftColors.COLORS.length; i++) {
            MinecraftColors.ColorEntry color =
                    MinecraftColors.COLORS[i];

            int column = i % 8;
            int row = i / 8;

            int x =
                    startX
                            + column * (CELL_SIZE + CELL_GAP);

            int y =
                    startY
                            + row * (CELL_SIZE + CELL_GAP);

            addColorButton(
                    invoker,
                    access,
                    color,
                    x,
                    y
            );
        }
    }

    private static void addColorButton(
            ScreenInvoker invoker,
            SignEditScreenAccess access,
            MinecraftColors.ColorEntry color,
            int x,
            int y
    ) {
        SignColorButton button =
                new SignColorButton(
                        x,
                        y,
                        CELL_SIZE,
                        CELL_SIZE,
                        color.rgb(),
                        () -> applyColor(
                                access,
                                color.rgb()
                        )
                );

        invoker.signedit$addRenderableWidget(button);
    }

    private static void applyColor(
            SignEditScreenAccess access,
            int color
    ) {
        access.signedit$selectColor(color);
    }
}
