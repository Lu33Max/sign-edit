package de.lumax.signedit.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class SignEditSettingsButton extends AbstractWidget {

    private final Runnable onPress;

    public SignEditSettingsButton(int x, int y, Runnable onPress) {
        super(x, y, 20, 20, Component.translatable("signedit.config.open"));
        this.onPress = onPress;
        setTooltip(Tooltip.create(Component.translatable("signedit.config.tooltip")));
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        graphics.fill(x, y, x + width, y + height, isHovered() ? 0xFF686868 : 0xFF383838);
        graphics.outline(x, y, width, height, 0xFF000000);

        int color = isHovered() ? 0xFFFFFFFF : 0xFFE0E0E0;
        for (int line = 0; line < 3; line++) {
            int lineY = y + 5 + line * 5;
            graphics.fill(x + 5, lineY, x + 15, lineY + 1, color);
        }
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
