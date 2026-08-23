package de.lumax.signedit.mixin;

import de.lumax.signedit.server.PendingSignFormatting;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.FilteredText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {

    @Shadow
    protected ServerPlayer player;

    @Inject(
            method = "updateSignText",
            at = @At("TAIL")
    )
    private void signedit$afterVanillaSignUpdate(
            ServerboundSignUpdatePacket packet,
            List<FilteredText> lines,
            CallbackInfo ci
    ) {
        System.out.println(
                "[SignEdit] updateSignText intercepted: "
                        + packet.getPos()
        );

        var payload = PendingSignFormatting.vanillaUpdateObserved(
                this.player.getUUID(),
                packet.getPos(),
                packet.isFrontText()
        );

        System.out.println(
                "[SignEdit] matching payload: "
                        + (payload != null)
        );

        if (payload != null) {
            System.out.println("[SignEdit] applying formatting");

            de.lumax.signedit.server.SignFormattingServer.applyFromMixin(
                    this.player,
                    payload
            );
        }
    }
}