package de.lumax.signedit;

import de.lumax.signedit.access.SignEditScreenAccess;
import de.lumax.signedit.config.SignEditConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import org.lwjgl.glfw.GLFW;

public class SignEditClient implements ClientModInitializer {
    public static final String MOD_ID = "signedit";
    private static KeyMapping openEditorKey;

    private static void openEditor(Minecraft client) {
        if (client.player == null || client.level == null || client.gui.screen() != null) {
            return;
        }

        SignBlockEntity sign = null;
        boolean front = true;

        if (client.hitResult instanceof BlockHitResult hit) {
            BlockEntity blockEntity = client.level.getBlockEntity(hit.getBlockPos());
            if (blockEntity instanceof SignBlockEntity existing) {
                sign = existing;
                front = existing.isFacingFrontText(client.player);
            }
        }

        if (sign == null) {
            sign = new SignBlockEntity(
                    client.player.blockPosition(),
                    Blocks.OAK_SIGN.defaultBlockState()
            );
            sign.setLevel(client.level);
        }

        openEditor(client, sign, front, null, null, null, null, null);
    }

    public static void openEditor(
            Minecraft client,
            SignBlockEntity sign,
            boolean front,
            SignText initialFrontText,
            SignText initialBackText,
            WoodType initialWoodType,
            SignText wideFrontText,
            SignText wideBackText
    ) {
        AbstractSignEditScreen screen = sign instanceof HangingSignBlockEntity
                ? new HangingSignEditScreen(sign, front, false)
                : new SignEditScreen(sign, front, false);
        SignEditScreenAccess access = (SignEditScreenAccess) screen;
        access.signedit$setCustomScreen(true);
        client.gui.setScreen(screen);

        if (initialFrontText != null) {
            access.signedit$setSessionState(
                    initialFrontText,
                    initialBackText,
                    initialWoodType,
                    wideFrontText,
                    wideBackText
            );
        }
    }

    @Override
    public void onInitializeClient() {
        SignEditConfig.load();

        openEditorKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.signedit.open_editor",
                        GLFW.GLFW_KEY_I,
                        KeyMapping.Category.MISC
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEditorKey.consumeClick()) {
                openEditor(client);
            }
        });
    }
}
