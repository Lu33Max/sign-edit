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

        int displayColor = brightenColor(color);

        // Farbfläche
        graphics.fill(
                x + 2,
                y + 2,
                x + width - 2,
                y + height - 2,
                0xFF000000 | displayColor
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

    private int brightenColor(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));

        float h;
        float s;
        float v = max;

        float delta = max - min;

        if (max == 0.0f) {
            s = 0.0f;
        } else {
            s = delta / max;
        }

        if (delta == 0.0f) {
            h = 0.0f;
        } else if (max == r) {
            h = 60.0f * (((g - b) / delta) % 6.0f);
        } else if (max == g) {
            h = 60.0f * (((b - r) / delta) + 2.0f);
        } else {
            h = 60.0f * (((r - g) / delta) + 4.0f);
        }

        if (h < 0.0f) {
            h += 360.0f;
        }

        // Helligkeit proportional erhöhen
        v = Math.min(1.0f, v * 2.5f);

        float c = v * s;
        float x = c * (1.0f - Math.abs((h / 60.0f) % 2.0f - 1.0f));
        float m = v - c;

        float rr;
        float gg;
        float bb;

        if (h < 60.0f) {
            rr = c;
            gg = x;
            bb = 0.0f;
        } else if (h < 120.0f) {
            rr = x;
            gg = c;
            bb = 0.0f;
        } else if (h < 180.0f) {
            rr = 0.0f;
            gg = c;
            bb = x;
        } else if (h < 240.0f) {
            rr = 0.0f;
            gg = x;
            bb = c;
        } else if (h < 300.0f) {
            rr = x;
            gg = 0.0f;
            bb = c;
        } else {
            rr = c;
            gg = 0.0f;
            bb = x;
        }

        int red = Math.min(255, Math.round((rr + m) * 255.0f));
        int green = Math.min(255, Math.round((gg + m) * 255.0f));
        int blue = Math.min(255, Math.round((bb + m) * 255.0f));

        return (red << 16) | (green << 8) | blue;
    }
}