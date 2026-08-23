package de.lumax.client.mixin;

import de.lumax.signedit.access.SignEditHexFieldAccess;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin {

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD")
    )
    private void signedit$mouseClicked(
            MouseButtonEvent event,
            boolean doubleClick,
            CallbackInfoReturnable<Boolean> cir
    ) {
        ContainerEventHandler handler =
                (ContainerEventHandler) (Object) this;

        if (handler instanceof SignEditHexFieldAccess access) {
            access.signedit$handleScreenMouseClick(
                    event.x(),
                    event.y(),
                    event.button()
            );
        }
    }
}