package de.lumax.signedit.gui;

import de.lumax.client.mixin.ScreenInvoker;
import de.lumax.signedit.access.SignEditScreenAccess;
import de.lumax.signedit.color.HexColorField;
import de.lumax.signedit.color.SignColorPicker;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public final class SignEditLayout {

    private static final int PICKER_X_OFFSET = -185;
    private static final int PICKER_Y_OFFSET = -30;
    private static final int FIELD_GAP = 5;
    private static final int BUTTON_WIDTH = 30;
    private static final int BUTTON_HEIGHT = 20;

    private final SignColorPicker colorPicker;
    private final HexColorField hexField;
    private final SignFormattingToolbar toolbar;

    private SignEditLayout(
            SignColorPicker colorPicker,
            HexColorField hexField,
            SignFormattingToolbar toolbar
    ) {
        this.colorPicker = colorPicker;
        this.hexField = hexField;
        this.toolbar = toolbar;
    }

    public static SignEditLayout addTo(
            Screen screen,
            SignEditScreenAccess access,
            Runnable resetColor
    ) {
        ScreenInvoker invoker = (ScreenInvoker) screen;
        int pickerX = screen.width / 2 + PICKER_X_OFFSET;
        int pickerY = screen.height / 4 + PICKER_Y_OFFSET;

        SignColorPicker[] colorPickerRef = new SignColorPicker[1];
        HexColorField[] hexFieldRef = new HexColorField[1];
        SignColorPicker colorPicker = new SignColorPicker(
                pickerX,
                pickerY,
                0xFF5555,
                () -> {
                    int color = colorPickerRef[0].getColor();
                    hexFieldRef[0].setColor(color);
                    access.signedit$selectColor(color);
                }
        );
        colorPickerRef[0] = colorPicker;

        int fieldX = colorPicker.getX();
        int fieldY = colorPicker.getY() + colorPicker.getHeight() + FIELD_GAP;
        HexColorField hexField = new HexColorField(
                fieldX,
                fieldY,
                50,
                BUTTON_HEIGHT,
                colorPicker.getColor(),
                color -> {
                    colorPicker.setColor(color);
                    access.signedit$selectColor(color);
                },
                    () -> invoker.signedit$setInitialFocus(hexFieldRef[0])
        );
            hexFieldRef[0] = hexField;

        Button resetButton = ResetColorButton.create(
                fieldX + hexField.getWidth(),
                fieldY,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                resetColor
        );
        Button applyButton = ApplyColorButton.create(
                resetButton.getX() + resetButton.getWidth(),
                resetButton.getY(),
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                colorPicker::getColor,
                access::signedit$selectColor
        );

        int toolbarX = pickerX + 1;
        int toolbarY = fieldY + hexField.getHeight() + FIELD_GAP;
        SignFormattingToolbar toolbar = SignFormattingToolbar.addTo(
            screen,
            access,
            toolbarX,
            toolbarY
        );

        int paletteY = pickerY - 33;
        SignColorPalette.addTo(screen, access, pickerX, paletteY);

        invoker.signedit$addRenderableWidget(colorPicker);
        invoker.signedit$addRenderableWidget(hexField);
        invoker.signedit$addRenderableWidget(resetButton);
        invoker.signedit$addRenderableWidget(applyButton);

        return new SignEditLayout(colorPicker, hexField, toolbar);
    }

    public SignColorPicker getColorPicker() {
        return colorPicker;
    }

    public HexColorField getHexField() {
        return hexField;
    }

    public SignFormattingToolbar getToolbar() {
        return toolbar;
    }
}