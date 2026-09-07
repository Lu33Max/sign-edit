package de.lumax.signedit.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class ResetColorButton {

    private ResetColorButton() {
    }

    public static Button create(
            int x,
            int y,
            int width,
            int height,
            Runnable resetColor
    ) {
        return Button.builder(
                Component.literal("Reset"),
                button -> resetColor.run()
        ).bounds(x, y, width, height).build();
    }
}