package de.lumax.signedit.color;

public final class ColorPickerUtil {

    private ColorPickerUtil() {
    }

    public static int hsvToRgb(
            float hue,
            float saturation,
            float brightness
    ) {
        float h = hue * 6.0f;

        int sector = (int) Math.floor(h);

        float fraction = h - sector;

        float p =
                brightness
                        * (1.0f - saturation);

        float q =
                brightness
                        * (1.0f
                        - saturation * fraction);

        float t =
                brightness
                        * (1.0f
                        - saturation
                        * (1.0f - fraction));

        float r;
        float g;
        float b;

        switch (sector % 6) {
            case 0 -> {
                r = brightness;
                g = t;
                b = p;
            }
            case 1 -> {
                r = q;
                g = brightness;
                b = p;
            }
            case 2 -> {
                r = p;
                g = brightness;
                b = t;
            }
            case 3 -> {
                r = p;
                g = q;
                b = brightness;
            }
            case 4 -> {
                r = t;
                g = p;
                b = brightness;
            }
            default -> {
                r = brightness;
                g = p;
                b = q;
            }
        }

        return (
                ((int) (r * 255.0f) << 16)
                        | ((int) (g * 255.0f) << 8)
                        | (int) (b * 255.0f)
        );
    }
}