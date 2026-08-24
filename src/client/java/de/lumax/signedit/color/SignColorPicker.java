package de.lumax.signedit.color;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class SignColorPicker extends AbstractWidget {

    private static final int SQUARE_SIZE = 90;
    private static final int HUE_WIDTH = 14;
    private static final int GAP = 6;

    private final ColorPickerState state;

    private boolean draggingSquare;
    private boolean draggingHue;

    private final Runnable onColorChanged;

    public SignColorPicker(
            int x,
            int y,
            int initialColor,
            Runnable onColorChanged
    ) {
        super(
                x,
                y,
                SQUARE_SIZE + GAP + HUE_WIDTH,
                SQUARE_SIZE,
                Component.empty()
        );

        this.state = new ColorPickerState(initialColor);
        this.onColorChanged = onColorChanged;
    }

    public int getColor() {
        return state.getRgb();
    }

    public void setColor(int color) {
        state.setRgb(color);
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderSaturationBrightness(
                graphics
        );

        renderHue(
                graphics
        );

        renderMarkers(
                graphics
        );
    }

    @Override
    public boolean mouseClicked(
            final MouseButtonEvent event, final boolean doubleClick
    ) {
        if (event.button() != 0) {
            return false;
        }

        var mouseX = event.x();
        var mouseY = event.y();

        if (isInsideSaturationBrightness(mouseX, mouseY)) {
            draggingSquare = true;
            updateSaturationBrightness(mouseX, mouseY);
            return true;
        }

        if (isInsideHue(mouseX, mouseY)) {
            draggingHue = true;
            updateHue(mouseY);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(
            final MouseButtonEvent event, final double dx, final double dy
    ) {
        if (event.button() != 0) {
            return false;
        }

        if (draggingSquare) {
            updateSaturationBrightness(
                    event.x(),
                    event.y()
            );
            return true;
        }

        if (draggingHue) {
            updateHue(event.y());
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(
            final MouseButtonEvent event
    ) {
        if (event.button() == 0) {
            draggingSquare = false;
            draggingHue = false;
        }

        return super.mouseReleased(event);
    }

    @Override
    protected void updateWidgetNarration(
            NarrationElementOutput narrationElementOutput
    ) {
    }

    private void renderSaturationBrightness(
            GuiGraphicsExtractor graphics
    ) {
        int x = getX();
        int y = getY();

        int hueColor = ColorPickerUtil.hsvToRgb(
                state.getHue(),
                1.0f,
                1.0f
        );

        // Horizontales Weiß → Hue
        for (int px = 0; px < SQUARE_SIZE; px++) {

            float saturation =
                    px / (float) (SQUARE_SIZE - 1);

            int rgb =
                    ColorPickerUtil.hsvToRgb(
                            state.getHue(),
                            saturation,
                            1.0f
                    );

            graphics.fill(
                    x + px,
                    y,
                    x + px + 1,
                    y + SQUARE_SIZE,
                    0xFF000000 | rgb
            );
        }

        // Schwarz-Overlay
        for (int py = 0; py < SQUARE_SIZE; py++) {

            float brightness =
                    1.0f
                            - py
                            / (float) (SQUARE_SIZE - 1);

            int alpha =
                    (int) ((1.0f - brightness) * 255.0f);

            graphics.fill(
                    x,
                    y + py,
                    x + SQUARE_SIZE,
                    y + py + 1,
                    (alpha << 24)
            );
        }
    }

    private void renderHue(
            GuiGraphicsExtractor graphics
    ) {
        int x =
                getX()
                        + SQUARE_SIZE
                        + GAP;

        int y = getY();

        for (int py = 0; py < SQUARE_SIZE; py++) {

            float hue =
                    py
                            / (float) (SQUARE_SIZE - 1);

            int color =
                    ColorPickerUtil.hsvToRgb(
                            hue,
                            1.0f,
                            1.0f
                    );

            graphics.fill(
                    x,
                    y + py,
                    x + HUE_WIDTH,
                    y + py + 1,
                    0xFF000000 | color
            );
        }
    }

    private void renderMarkers(
            GuiGraphicsExtractor graphics
    ) {
        int squareX = getX();
        int squareY = getY();

        int markerX =
                squareX
                        + (int) (
                        state.getSaturation()
                                * (SQUARE_SIZE - 1)
                );

        int markerY =
                squareY
                        + (int) (
                        (1.0f - state.getBrightness())
                                * (SQUARE_SIZE - 1)
                );

        graphics.outline(
                markerX - 3,
                markerY - 3,
                7,
                7,
                0xFFFFFFFF
        );

        graphics.outline(
                markerX - 2,
                markerY - 2,
                5,
                5,
                0xFF000000
        );

        // Hue marker
        int hueX =
                getX()
                        + SQUARE_SIZE
                        + GAP;

        int hueY =
                getY()
                        + (int) (
                        state.getHue()
                                * (SQUARE_SIZE - 1)
                );

        graphics.outline(
                hueX - 2,
                hueY - 2,
                HUE_WIDTH + 4,
                5,
                0xFFFFFFFF
        );
    }

    private boolean isInsideSaturationBrightness(
            double mouseX,
            double mouseY
    ) {
        return mouseX >= getX()
                && mouseX < getX() + SQUARE_SIZE
                && mouseY >= getY()
                && mouseY < getY() + SQUARE_SIZE;
    }

    private boolean isInsideHue(
            double mouseX,
            double mouseY
    ) {
        int hueX =
                getX()
                        + SQUARE_SIZE
                        + GAP;

        return mouseX >= hueX
                && mouseX < hueX + HUE_WIDTH
                && mouseY >= getY()
                && mouseY < getY() + SQUARE_SIZE;
    }

    private void updateSaturationBrightness(
            double mouseX,
            double mouseY
    ) {
        float saturation =
                (float) (
                        (mouseX - getX())
                                / (double) (SQUARE_SIZE - 1)
                );

        float brightness =
                1.0f
                        - (float) (
                        (mouseY - getY())
                                / (double) (SQUARE_SIZE - 1)
                );

        saturation =
                Math.clamp(
                        saturation
                        ,
                        0.0f,
                        1.0f);

        brightness =
                Math.clamp(
                        brightness
                        ,
                        0.0f,
                        1.0f);

        state.setHsb(
                state.getHue(),
                saturation,
                brightness
        );

        colorChanged();
    }

    private void updateHue(
            double mouseY
    ) {
        float hue =
                (float) (
                        (mouseY - getY())
                                / (double) (SQUARE_SIZE - 1)
                );

        hue =
                Math.clamp(
                        hue
                        ,
                        0.0f,
                        1.0f);

        state.setHsb(
                hue,
                state.getSaturation(),
                state.getBrightness()
        );

        colorChanged();
    }

    private void colorChanged() {
        onColorChanged.run();
    }
}