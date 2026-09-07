package de.lumax.signedit.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public final class ApplyColorButton {

    private ApplyColorButton() {
    }

    public static Button create(
            int x,
            int y,
            int width,
            int height,
            IntSupplier color,
            IntConsumer applyColor
    ) {
        return Button.builder(
                Component.literal("Apply"),
                button -> applyColor.accept(color.getAsInt())
        ).bounds(x, y, width, height).build();
    }
}