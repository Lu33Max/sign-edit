package de.lumax.signedit.color;

public class ColorPickerState {

    private float hue;
    private float saturation;
    private float brightness;

    public ColorPickerState(int rgb) {
        setRgb(rgb);
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setHsb(
            float hue,
            float saturation,
            float brightness
    ) {
        this.hue = clamp(hue, 0.0f, 1.0f);
        this.saturation = clamp(saturation, 0.0f, 1.0f);
        this.brightness = clamp(brightness, 0.0f, 1.0f);
    }

    public void setRgb(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));

        float delta = max - min;

        brightness = max;

        if (max == 0.0f) {
            saturation = 0.0f;
        } else {
            saturation = delta / max;
        }

        if (delta == 0.0f) {
            // Hue is undefined for grayscale colors. Keep the previous hue so
            // dragging toward black does not make the hue selector jump to red.
            return;
        }

        if (max == r) {
            hue = ((g - b) / delta) % 6.0f;
        } else if (max == g) {
            hue = ((b - r) / delta) + 2.0f;
        } else {
            hue = ((r - g) / delta) + 4.0f;
        }

        hue /= 6.0f;

        if (hue < 0.0f) {
            hue += 1.0f;
        }
    }

    public int getRgb() {
        return ColorPickerUtil.hsvToRgb(hue, saturation, brightness);
    }

    private static float clamp(
            float value,
            float min,
            float max
    ) {
        return Math.max(min, Math.min(max, value));
    }
}
