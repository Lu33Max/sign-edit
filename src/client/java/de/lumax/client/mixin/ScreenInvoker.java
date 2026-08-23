package de.lumax.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenInvoker {

    @Accessor("minecraft")
    Minecraft signedit$getMinecraft();

    @Invoker("addRenderableWidget")
    <T extends GuiEventListener & Renderable & NarratableEntry>
    T signedit$addRenderableWidget(T widget);

    @Invoker("setInitialFocus")
    void signedit$setInitialFocus(
            GuiEventListener target
    );

    @Invoker("clearFocus")
    void signedit$clearFocus();
}
