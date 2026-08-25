package de.lumax.signedit.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class SignColorButton extends AbstractWidget {

    private final int color;
    private final Runnable onPress;

    public SignColorButton(
            int x,
            int y,
            int width,
            int height,
            int color,
            Runnable onPress
    ) {
        super(
                x,
                y,
                width,
                height,
                Component.empty()
        );

        this.color = color;
        this.onPress = onPress;
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        int x = getX();
        int y = getY();

        // Äußerer Rand
        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF000000
        );

        // Farbfläche
        graphics.fill(
                x + 2,
                y + 2,
                x + width - 2,
                y + height - 2,
                0xFF000000 | color
        );

        // Hover-Rand
        if (isHovered()) {
            graphics.outline(
                    x,
                    y,
                    width,
                    height,
                    0xFFFFFFFF
            );
        }
    }

    @Override
    public void onClick(
            final @NonNull MouseButtonEvent event, final boolean doubleClick
    ) {
        onPress.run();
    }

    @Override
    protected void updateWidgetNarration(
            net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput
    ) {
        defaultButtonNarrationText(
                narrationElementOutput
        );
    }
}