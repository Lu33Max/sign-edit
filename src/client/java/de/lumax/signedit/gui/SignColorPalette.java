package de.lumax.signedit.gui;

import de.lumax.client.mixin.ScreenInvoker;
import de.lumax.signedit.access.SignEditScreenAccess;
import de.lumax.signedit.text.MinecraftColors;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SignColorPalette {

    private static final int CELL_SIZE = 12;
    private static final int CELL_GAP = 2;

    private SignColorPalette() {
    }

    public static void addTo(
            Screen screen,
            SignEditScreenAccess access
    ) {
        ScreenInvoker invoker =
                (ScreenInvoker) screen;

        int gridWidth =
                8 * CELL_SIZE
                        + 7 * CELL_GAP;

        int gridHeight =
                2 * CELL_SIZE
                        + CELL_GAP;

        int startX =
                screen.width / 2
                        - 75
                        - gridWidth;

        int startY =
                screen.height / 4
                        - gridHeight / 2
                        - 50;

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
