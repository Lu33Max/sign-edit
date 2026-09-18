package de.lumax.signedit.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SignEditConfigScreen extends Screen {

    private static final int DONE_BUTTON_WIDTH = 200;

    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private @Nullable SignEditOptionsList list;
    private @Nullable CycleButton<AutoLineBreakMode> autoLineBreakButton;
    private @Nullable CycleButton<Boolean> wrapToFirstLineButton;

    public SignEditConfigScreen(Screen parent) {
        super(Component.translatable("signedit.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        layout.addTitleHeader(title, font);

        list = layout.addToContents(new SignEditOptionsList(
                minecraft,
                width,
                layout.getContentHeight(),
                layout.getHeaderHeight()
        ));
        addOptions(list);

        layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .width(DONE_BUTTON_WIDTH)
                .build());

        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    /**
     * Registers every config category and setting shown on this screen. Add further
     * {@code list.addCategory(...)} / {@code list.addSetting(...)} calls here as new
     * settings are introduced - the surrounding layout and scroll handling requires no
     * changes.
     */
    private void addOptions(SignEditOptionsList list) {
        list.addCategory(Component.translatable("signedit.config.category.editing"));
        list.addSetting(
                Component.translatable("signedit.config.automatic_line_break"),
                createAutoLineBreakButton(),
                this::resetAutoLineBreak
        );
        list.addSetting(
                Component.translatable("signedit.config.wrap_to_first_line"),
                createWrapToFirstLineButton(),
                this::resetWrapToFirstLine
        );
    }

    private CycleButton<AutoLineBreakMode> createAutoLineBreakButton() {
        autoLineBreakButton = CycleButton.builder(AutoLineBreakMode::getDisplayName, SignEditConfig.getAutoLineBreakMode())
                .withValues(AutoLineBreakMode.values())
                .displayOnlyValue()
                .create(
                        0, 0, 0, 20,
                        Component.translatable("signedit.config.automatic_line_break"),
                        (button, value) -> {
                            SignEditConfig.setAutoLineBreakMode(value);
                            updateWrapToFirstLineAvailability(value);
                        }
                );
        return autoLineBreakButton;
    }

    private void resetAutoLineBreak() {
        SignEditConfig.setAutoLineBreakMode(AutoLineBreakMode.DEFAULT);
        if (autoLineBreakButton != null) {
            autoLineBreakButton.setValue(AutoLineBreakMode.DEFAULT);
        }
        updateWrapToFirstLineAvailability(AutoLineBreakMode.DEFAULT);
    }

    private CycleButton<Boolean> createWrapToFirstLineButton() {
        wrapToFirstLineButton = CycleButton.onOffBuilder(SignEditConfig.isWrapToFirstLineEnabled())
                .displayOnlyValue()
                .create(
                        0, 0, 0, 20,
                        Component.translatable("signedit.config.wrap_to_first_line"),
                        (button, value) -> SignEditConfig.setWrapToFirstLineEnabled(value)
                );
        wrapToFirstLineButton.active = SignEditConfig.getAutoLineBreakMode() != AutoLineBreakMode.OFF;
        return wrapToFirstLineButton;
    }

    private void resetWrapToFirstLine() {
        SignEditConfig.setWrapToFirstLineEnabled(SignEditConfig.DEFAULT_WRAP_TO_FIRST_LINE);
        if (wrapToFirstLineButton != null) {
            wrapToFirstLineButton.setValue(SignEditConfig.DEFAULT_WRAP_TO_FIRST_LINE);
        }
    }

    private void updateWrapToFirstLineAvailability(AutoLineBreakMode mode) {
        if (wrapToFirstLineButton != null) {
            wrapToFirstLineButton.active = mode != AutoLineBreakMode.OFF;
        }
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        if (list != null) {
            list.updateSize(width, layout);
        }
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }
}
