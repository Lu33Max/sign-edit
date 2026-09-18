package de.lumax.signedit.gui;

import de.lumax.signedit.access.SignEditScreenAccess;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class SignTypeButton {

    private SignTypeButton() {
    }

    public static Button create(
            int x,
            int y,
            int width,
            int height,
            SignEditScreenAccess access
    ) {
        return Button.builder(
                label(access),
                ignored -> access.signedit$toggleSignType()
        ).bounds(x, y, width, height).build();
    }

    private static Component label(SignEditScreenAccess access) {
        return Component.literal(access.signedit$isHangingSign() ? "Hanging" : "Regular");
    }
}
