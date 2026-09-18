package de.lumax.signedit.gui;

import de.lumax.signedit.mixin.ScreenInvoker;
import de.lumax.signedit.access.SignEditScreenAccess;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class WoodTypeButtons {

    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int LABEL_WIDTH = 70;

    private WoodTypeButtons() {
    }

    public static void addTo(
            Screen screen,
            SignEditScreenAccess access,
            int y
    ) {
        ScreenInvoker invoker = (ScreenInvoker) screen;
        Button[] labelRef = new Button[1];

        Button previousButton = Button.builder(
                Component.literal("<"),
                ignored -> {
                    access.signedit$cycleWoodType(-1);
                    updateLabel(labelRef[0], access);
                }
        ).bounds(screen.width / 2 - 55, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        Button label = Button.builder(
                Component.literal(access.signedit$getWoodType().name()),
                ignored -> {
                }
        ).bounds(screen.width / 2 - 35, y, LABEL_WIDTH, BUTTON_HEIGHT).build();
        label.active = false;
        labelRef[0] = label;

        Button nextButton = Button.builder(
                Component.literal(">"),
                ignored -> {
                    access.signedit$cycleWoodType(1);
                    updateLabel(labelRef[0], access);
                }
        ).bounds(screen.width / 2 + 35, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        invoker.signedit$addRenderableWidget(previousButton);
        invoker.signedit$addRenderableWidget(label);
        invoker.signedit$addRenderableWidget(nextButton);
    }

    private static void updateLabel(
            Button label,
            SignEditScreenAccess access
    ) {
        label.setMessage(Component.literal(access.signedit$getWoodType().name()));
    }
}
