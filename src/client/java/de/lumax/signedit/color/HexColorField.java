package de.lumax.signedit.color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class HexColorField extends EditBox {

    private final Consumer<Integer> onColorChanged;
    private final Runnable onFocused;
    private boolean updating;

    public HexColorField(
            int x,
            int y,
            int width,
            int height,
            int initialColor,
            Consumer<Integer> onColorChanged,
            Runnable onFocused
    ) {
        super(
                Minecraft.getInstance().font,
                x,
                y,
                width,
                height,
                Component.literal("Hex Color")
        );

        this.onColorChanged = onColorChanged;
        this.onFocused = onFocused;

        setMaxLength(7);
        setValue(toHex(initialColor));

        setResponder(this::handleInput);
    }

    @Override
    public boolean mouseClicked(
            final MouseButtonEvent event, final boolean doubleClick
    ) {
        boolean result = super.mouseClicked(
                event, doubleClick
        );

        if (result) {
            setFocused(true);
            onFocused.run();
        }

        return result;
    }

    private void handleInput(String value) {
        if (updating) {
            return;
        }

        Integer color = parseHex(value);

        if (color != null) {
            onColorChanged.accept(color);
        }
    }

    public void setColor(int color) {
        updating = true;

        setValue(toHex(color));

        updating = false;
    }

    private static String toHex(int color) {
        return String.format(
                "#%06X",
                color & 0xFFFFFF
        );
    }

    private static Integer parseHex(String value) {
        if (value == null) {
            return null;
        }

        String hex = value.trim();

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 6) {
            return null;
        }

        try {
            return Integer.parseInt(
                    hex,
                    16
            );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}