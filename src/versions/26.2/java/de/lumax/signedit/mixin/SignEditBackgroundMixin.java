package de.lumax.signedit.mixin;

import de.lumax.signedit.access.SignEditScreenAccess;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SignEditScreen.class)
public abstract class SignEditBackgroundMixin {

    @ModifyArg(
            method = "extractSignBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
            ),
            index = 1
    )
    private Identifier signedit$selectedWoodTexture(Identifier texture) {
        SignEditScreenAccess access = (SignEditScreenAccess) this;
        if (!access.signedit$isCustomScreen()) {
            return texture;
        }

        return Identifier.withDefaultNamespace(
                "textures/gui/signs/" + access.signedit$getWoodType().name() + ".png"
        );
    }
}