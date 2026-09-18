package de.lumax.signedit.axiom;

import java.util.Map;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class AxiomPlacementSender {

    private static final Identifier CHANNEL =
            Identifier.fromNamespaceAndPath("axiom", "set_block");

        public static final CustomPacketPayload.Type<Payload> TYPE =
            new CustomPacketPayload.Type<>(CHANNEL);

        public static final StreamCodec<RegistryFriendlyByteBuf, Payload> CODEC =
                StreamCodec.of(
                    (buffer, payload) -> payload.write(buffer),
                    Payload::read
                );

    private AxiomPlacementSender() {
    }

    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("axiom");
    }

    public static boolean canUseDirectPlacement() {
        return false;
    }

    public static void send(
            BlockPos pos,
            BlockState state,
            ItemStack item,
            BlockHitResult hit
    ) {
        ClientPlayNetworking.send(new Payload(pos, state, item, hit));
    }

    public record Payload(
            BlockPos pos,
            BlockState state,
            ItemStack item,
            BlockHitResult hit
    ) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static Payload read(RegistryFriendlyByteBuf buffer) {
            throw new UnsupportedOperationException("Axiom placement payload is serverbound");
        }

        public void write(RegistryFriendlyByteBuf buffer) {
            buffer.writeMap(
                    Map.of(pos, state),
                    RegistryFriendlyByteBuf::writeBlockPos,
                        (output, value) -> output.writeVarInt(
                            net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY.getId(value)
                        )
            );
            buffer.writeBoolean(false);
            buffer.writeVarInt(1);
            buffer.writeBoolean(false);
            buffer.writeBlockHitResult(hit);
            buffer.writeEnum(InteractionHand.MAIN_HAND);
            buffer.writeVarInt(0);
        }
    }
}