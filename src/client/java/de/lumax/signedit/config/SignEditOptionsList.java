package de.lumax.signedit.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;

/**
 * Scrollable settings list used by {@link SignEditConfigScreen}, styled after the settings
 * screens of common Fabric mods (e.g. Accurate Block Placement, Double Hotbar): a bold
 * headline separates each group of settings, and every setting is its own row with the
 * name on the left, the control to change it in the middle, and a "Reset" button on the
 * right to restore its default value.
 * <p>
 * New settings can be added at any time via {@link #addCategory(Component)} and
 * {@link #addSetting(Component, AbstractWidget, Runnable)} without touching the rest of
 * the screen.
 */
public final class SignEditOptionsList extends ContainerObjectSelectionList<SignEditOptionsList.Entry> {

    private static final int ROW_WIDTH = 310;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_SPACING = 4;
    private static final int CATEGORY_LINE_HEIGHT = 9;
    private static final int CATEGORY_GAP = CATEGORY_LINE_HEIGHT * 2;
    private static final int LABEL_WIDTH = 140;
    private static final int RESET_BUTTON_WIDTH = 46;

    public SignEditOptionsList(Minecraft minecraft, int width, int height, int y) {
        super(minecraft, width, height, y, ROW_HEIGHT + ROW_SPACING);
        this.centerListVertically = false;
    }

    @Override
    public int getRowWidth() {
        return ROW_WIDTH;
    }

    /**
     * Adds a bold headline separating a group of settings, e.g. "Editing".
     */
    public void addCategory(Component title) {
        int paddingTop = this.children().isEmpty() ? 0 : CATEGORY_GAP;
        this.addEntry(new CategoryEntry(title, paddingTop), paddingTop + CATEGORY_LINE_HEIGHT + ROW_SPACING);
    }

    /**
     * Adds a setting row: {@code label} on the left, {@code control} to change the value in
     * the middle, and a "Reset" button on the right that runs {@code onResetToDefault}
     * (which should both restore the config value and update {@code control}'s displayed
     * value).
     */
    public void addSetting(Component label, AbstractWidget control, Runnable onResetToDefault) {
        Button resetButton = Button.builder(
                Component.translatable("controls.reset"),
                button -> onResetToDefault.run()
        ).build();
        this.addEntry(new SettingEntry(label, control, resetButton));
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
    }

    private static final class CategoryEntry extends Entry {

        private final StringWidget widget;
        private final int paddingTop;

        private CategoryEntry(Component title, int paddingTop) {
            this.paddingTop = paddingTop;
            this.widget = new StringWidget(
                    title.copy().withStyle(Style.EMPTY.withBold(true)),
                    Minecraft.getInstance().font
            );
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            widget.setPosition(getContentX(), getContentY() + paddingTop);
            widget.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(widget);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(widget);
        }
    }

    private static final class SettingEntry extends Entry {

        private final StringWidget label;
        private final AbstractWidget control;
        private final AbstractWidget resetButton;

        private SettingEntry(Component label, AbstractWidget control, AbstractWidget resetButton) {
            this.label = new StringWidget(label, Minecraft.getInstance().font);
            this.control = control;
            this.resetButton = resetButton;

            int controlWidth = ROW_WIDTH - LABEL_WIDTH - RESET_BUTTON_WIDTH - ROW_SPACING * 2;
            control.setWidth(controlWidth);
            resetButton.setWidth(RESET_BUTTON_WIDTH);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            int x = getContentX();
            int y = getContentY();

            label.setPosition(x, y + (getContentHeight() - label.getHeight()) / 2);
            label.extractRenderState(graphics, mouseX, mouseY, a);
            x += LABEL_WIDTH + ROW_SPACING;

            control.setPosition(x, y);
            control.extractRenderState(graphics, mouseX, mouseY, a);
            x += control.getWidth() + ROW_SPACING;

            resetButton.setPosition(x, y);
            resetButton.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(label, control, resetButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(label, control, resetButton);
        }
    }
}
