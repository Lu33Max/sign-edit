package de.lumax.signedit.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SignEditConfigScreen extends Screen {

    private static final int CONTENT_WIDTH = 400;
    private static final int HORIZONTAL_MARGIN = 20;
    private static final int MODE_BUTTON_WIDTH = 150;

    private final Screen parent;

    public SignEditConfigScreen(Screen parent) {
        super(Component.translatable("signedit.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int contentWidth = Math.min(
                CONTENT_WIDTH,
                this.width - 2 * HORIZONTAL_MARGIN
        );
        int contentLeft = (this.width - contentWidth) / 2;
        int rowY = this.height / 2 - 10;
        addRenderableWidget(new StringWidget(
                contentLeft,
                rowY + 6,
                Component.translatable("signedit.config.automatic_line_break"),
                this.font
        ));

        Button modeButton = Button.builder(
                modeLabel(),
                button -> {
                    SignEditConfig.setAutoLineBreakMode(
                            SignEditConfig.getAutoLineBreakMode().next()
                    );
                    button.setMessage(modeLabel());
                }
        ).bounds(
                contentLeft + contentWidth - MODE_BUTTON_WIDTH,
                rowY,
                MODE_BUTTON_WIDTH,
                20
        ).build();
        addRenderableWidget(modeButton);

        addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                button -> onClose()
        ).bounds(contentLeft, this.height / 2 + 20, contentWidth, 20).build());
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }

    private static Component modeLabel() {
        return SignEditConfig.getAutoLineBreakMode().getDisplayName();
    }
}
