package de.lumax.client.mixin;

import de.lumax.signedit.access.SignEditHexFieldAccess;
import de.lumax.signedit.access.SignEditScreenAccess;
import de.lumax.signedit.access.TextFieldHelperAccess;
import de.lumax.signedit.color.HexColorField;
import de.lumax.signedit.color.SignColorPicker;
import de.lumax.signedit.gui.SignColorPalette;
import de.lumax.signedit.gui.SignFormattingToolbar;
import de.lumax.signedit.server.SignFormattingPayloadFactory;
import de.lumax.signedit.text.SignTextModel;
import de.lumax.signedit.text.FormattingType;
import de.lumax.signedit.text.TextStyle;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin implements SignEditScreenAccess, SignEditHexFieldAccess {

    @Shadow
    private TextFieldHelper signField;

    @Shadow
    private long cursorBlinkStartTime;

    @Shadow
    private int line;

    @Shadow
    private SignText text;

    @Final
    @Shadow
    protected SignBlockEntity sign;

    @Final
    @Shadow
    private boolean isFrontText;

    @Final
    @Shadow
    private String[] messages;

    @Unique
    @Override
    public int signedit$getCursorPos() {
        return this.signField.getCursorPos();
    }

    @Unique
    @Override
    public int signedit$getSelectionPos() {
        return this.signField.getSelectionPos();
    }

    @Unique
    @Override
    public int signedit$getCurrentLine() {
        return this.line;
    }

    @Unique
    @Override
    public boolean signedit$isSelecting() {
        return this.signField.isSelecting();
    }

    @Unique
    private final SignTextModel signedit$model = new SignTextModel();

    @Unique
    @Override
    public SignTextModel signedit$getModel() {
        return this.signedit$model;
    }

    @Unique
    private TextStyle signedit$activeStyle = TextStyle.EMPTY;

    @Unique
    private boolean signedit$clearToolbarFocusAfterClick;

    @Unique
    @Override
    public TextStyle signedit$getActiveStyle() {
        return this.signedit$activeStyle;
    }

    @Unique
    @Override
    public void signedit$clearToolbarFocusAfterClick() {
        this.signedit$clearToolbarFocusAfterClick = true;
    }

    @Unique
    @Override
    public void signedit$toggleFormatting(FormattingType type) {
        boolean enabled = switch (type) {
            case BOLD -> signedit$activeStyle.bold();
            case ITALIC -> signedit$activeStyle.italic();
            case UNDERLINED -> signedit$activeStyle.underlined();
            case STRIKETHROUGH -> signedit$activeStyle.strikethrough();
            case OBFUSCATED -> signedit$activeStyle.obfuscated();
        };
        boolean newValue = !enabled;
        int cursor = this.signField.getCursorPos();
        int selection = this.signField.getSelectionPos();
        int start = Math.min(cursor, selection);
        int end = Math.max(cursor, selection);

        if (start != end) {
            this.signedit$model.setFormatting(this.line, start, end, type, newValue);
        }

        this.signedit$activeStyle = this.signedit$activeStyle.withFormatting(type, newValue);
        signedit$updateToolbar();
    }

    @Unique
    @Override
    public void signedit$selectColor(int color) {
        int cursor = this.signField.getCursorPos();
        int selection = this.signField.getSelectionPos();
        int start = Math.min(cursor, selection);
        int end = Math.max(cursor, selection);

        if (start != end) {
            this.signedit$model.setColor(this.line, start, end, color);
        }

        this.signedit$activeStyle = this.signedit$activeStyle.withColor(color);

        if (this.signedit$colorPicker != null
                && this.signedit$colorPicker.getColor() != color) {
            this.signedit$colorPicker.setColor(color);
        }

        if (this.signedit$hexField != null && !this.signedit$hexField.isFocused()) {
            this.signedit$hexField.setColor(color);
        }
    }

    @Unique
    public void signedit$resetColor(){
        int cursor = this.signField.getCursorPos();
        int selection = this.signField.getSelectionPos();
        int start = Math.min(cursor, selection);
        int end = Math.max(cursor, selection);

        this.signedit$model.setColor(this.line, start, end, null);
        this.signedit$activeStyle = this.signedit$activeStyle.withColor(null);
    }

    @Unique
    private int signedit$getFormattedTextX(
            int line,
            int charIndex
    ) {
        Minecraft minecraft =
                ((ScreenInvoker) (Object) this).signedit$getMinecraft();

        String text = this.signedit$model
                .getLine(line)
                .getText();

        charIndex = Math.clamp(charIndex, 0, text.length());

        int fullWidth = minecraft.font.width(
                this.signedit$model.buildComponent(
                        line,
                        0,
                        text.length()
                )
        );

        int prefixWidth = minecraft.font.width(
                this.signedit$model.buildComponent(
                        line,
                        0,
                        charIndex
                )
        );

        return prefixWidth - fullWidth / 2;
    }

    @Unique
    private int signedit$getFormattedWidth(
            Font font,
            int lineIndex,
            int endIndex
    ) {
        String lineText = this.signedit$model
                .getLine(lineIndex)
                .getText();

        endIndex = Math.clamp(endIndex, 0, lineText.length());

        Component component = this.signedit$model.buildComponent(
                lineIndex,
                0,
                endIndex
        );

        return font.width(component);
    }

    @Unique
    private void signedit$applyPendingEdit(
            String message
    ) {
        TextFieldHelperAccess access =
                (TextFieldHelperAccess) this.signField;

        if (!access.signedit$hasPendingEdit()) {
            this.signedit$model.setLineText(
                    this.line,
                    message
            );
            return;
        }

        this.signedit$model.setLineText(this.line, message,
                access.signedit$getEditStart(), access.signedit$getEditEnd(),
                this.signedit$activeStyle);

        access.signedit$clearPendingEdit();
    }

    @Unique
    private int signedit$lastCursorPos = -1;

    @Unique
    private int signedit$lastLine = -1;

    @Unique
    private void signedit$updateFormattingFromCursor() {
        int cursor = this.signField.getCursorPos();
        int line = this.line;

        if (cursor == this.signedit$lastCursorPos
                && line == this.signedit$lastLine) {
            return;
        }

        this.signedit$lastCursorPos = cursor;
        this.signedit$lastLine = line;

        SignTextModel model = this.signedit$model;

        if (cursor < 0) {
            return;
        }

        // take formatting of character left of cursor
        int index = cursor - 1;

        String text = model.getLine(line).getText();

        if (index >= text.length()) {
            index = text.length() - 1;
        }

        if (index < 0) {
            return;
        }

        this.signedit$activeStyle = model.getStyleAt(line, index);
        signedit$updateToolbar();

        Integer color = this.signedit$activeStyle.color();

        if (color == null) {
            return;
        }

        if (this.signedit$colorPicker != null) {
            this.signedit$colorPicker.setColor(
                    color
            );
        }

        if (this.signedit$hexField != null
                && !this.signedit$hexField.isFocused()) {

            this.signedit$hexField.setColor(
                    color
            );
        }
    }

    @Unique
    private void signedit$updateToolbar() {
        if (this.signedit$toolbar != null) {
            this.signedit$toolbar.update(this.signedit$activeStyle);
        }
    }

    @ModifyArg(
            method = "extractSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/TextCursorUtils;extractAppendCursor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIIZ)V"
            ),
            index = 2
    )
    private int signedit$appendCursorX(int x) {
        return signedit$getFormattedTextX(
                this.line,
                this.signField.getCursorPos()
        );
    }

    @ModifyArg(
            method = "extractSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/TextCursorUtils;extractInsertCursor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIII)V"
            ),
            index = 1
    )
    private int signedit$insertCursorX(int x) {
        return signedit$getFormattedTextX(
                this.line,
                this.signField.getCursorPos()
        );
    }

    @Redirect(
            method = "extractSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III Z)V"
            )
    )
    private void signedit$renderFormattedText(
            GuiGraphicsExtractor graphics,
            Font font,
            String str,
            int x,
            int y,
            int color,
            boolean dropShadow
    ) {
        signedit$updateFormattingFromCursor();

        int lineHeight = this.sign.getTextLineHeight();

        int line = (y + 2 * lineHeight) / lineHeight;

        if (line < 0 || line >= 4) {
            graphics.text(
                    font,
                    str,
                    x,
                    y,
                    color,
                    dropShadow
            );
            return;
        }

        Component component =
                this.signedit$model.buildComponent(line);

        int formattedWidth =
                font.width(component);

        int formattedX =
                -formattedWidth / 2;

        graphics.text(
                font,
                component,
                formattedX,
                y,
                color,
                dropShadow
        );
    }

    @Unique
    private int signedit$getTextX(
            int line,
            int charIndex
    ) {
        String text = this.signedit$model
                .getLine(line)
                .getText();

        charIndex = Math.clamp(charIndex, 0, text.length());

        int fullWidth = signedit$getFormattedWidth(
                ((ScreenInvoker) (Object) this).signedit$getMinecraft().font,
                line,
                text.length()
        );

        int prefixWidth = signedit$getFormattedWidth(
                ((ScreenInvoker) (Object) this).signedit$getMinecraft().font,
                line,
                charIndex
        );

        return prefixWidth - fullWidth / 2;
    }

    @Redirect(
            method = "extractSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I",
                    ordinal = 0
            )
    )
    private int signedit$fullLineWidth(
            Font font,
            String text
    ) {
        return signedit$getFormattedWidth(
                font,
                this.line,
                text.length()
        );
    }

    @Redirect(
            method = "extractSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textHighlight(IIIIZ)V"
            )
    )
    private void signedit$renderSelection(
            GuiGraphicsExtractor graphics,
            int fromX,
            int fromY,
            int toX,
            int toY,
            boolean render
    ) {
        int line = this.line;

        String text = this.signedit$model
                .getLine(line)
                .getText();

        int cursorPos = this.signField.getCursorPos();
        int selectionPos = this.signField.getSelectionPos();

        int start = Math.min(cursorPos, selectionPos);
        int end = Math.max(cursorPos, selectionPos);

        start = Math.clamp(start, 0, text.length());
        end = Math.clamp(end, 0, text.length());

        if (start == end) {
            return;
        }

        Minecraft minecraft =
                ((ScreenInvoker) (Object) this).signedit$getMinecraft();

        int fullWidth = this.signedit$getFormattedWidth(
                minecraft.font,
                line,
                text.length()
        );

        int startWidth = this.signedit$getFormattedWidth(
                minecraft.font,
                line,
                start
        );

        int endWidth = this.signedit$getFormattedWidth(
                minecraft.font,
                line,
                end
        );

        int formattedFromX =
                Math.min(startWidth, endWidth) - fullWidth / 2;

        int formattedToX =
                Math.max(startWidth, endWidth) - fullWidth / 2;

        graphics.textHighlight(
                formattedFromX,
                fromY,
                formattedToX,
                toY,
                render
        );
    }

    @Unique
    private SignColorPicker signedit$colorPicker;

    @Unique
    private HexColorField signedit$hexField;

    @Unique
    private Button signedit$applyColorButton;

    @Unique
    private Button signedit$resetColorButton;

    @Unique
    private SignFormattingToolbar signedit$toolbar;

    @Inject(method = "init", at = @At("TAIL"))
    private void signedit$init(CallbackInfo ci) {
        this.signedit$model.loadFromSignText(
                this.text,
                false
        );

        AbstractSignEditScreen screen = (AbstractSignEditScreen) (Object) this;
        ScreenInvoker invoker = (ScreenInvoker) (Object) this;

        this.signedit$toolbar = SignFormattingToolbar.addTo(screen);
        SignColorPalette.addTo(
            screen,
            this
        );

        signedit$colorPicker = new SignColorPicker(
                screen.width / 2 - 185,
                screen.height / 4 - 30,
                0xFF5555,
                () -> {
                    int color = this.signedit$colorPicker.getColor();

                    this.signedit$hexField.setColor(color);
                    this.signedit$selectColor(color);
                }
        );

        this.signedit$hexField = new HexColorField(
                screen.width / 2 - 185,
                screen.height / 4 + 65,
                50,
                20,
                this.signedit$colorPicker.getColor(),
                color -> {
                    this.signedit$colorPicker.setColor(color);
                    this.signedit$selectColor(color);
                },
                () -> {
                    invoker.signedit$setInitialFocus(
                            this.signedit$hexField
                    );
                }
        );

        this.signedit$resetColorButton = Button.builder(
                Component.literal("Reset"),
                _ -> {
                    this.signedit$resetColor();
                }
        ).bounds(
                screen.width / 2 - 135,
                screen.height / 4 + 65,
                30,
                20
        ).build();

        this.signedit$applyColorButton = Button.builder(
                Component.literal("Apply"),
                button -> {
                    int color =
                            this.signedit$colorPicker.getColor();

                    this.signedit$selectColor(color);
                }
        ).bounds(
                screen.width / 2 - 105,
                screen.height / 4 + 65,
                30,
                20
        ).build();

        ((ScreenInvoker) (Object) this)
                .signedit$addRenderableWidget(
                        signedit$colorPicker
                );

        ((ScreenInvoker) (Object) this)
                .signedit$addRenderableWidget(
                        this.signedit$hexField
                );

        ((ScreenInvoker) (Object) this)
                .signedit$addRenderableWidget(
                        this.signedit$applyColorButton
                );

        ((ScreenInvoker) (Object) this)
                .signedit$addRenderableWidget(
                        this.signedit$resetColorButton
                );
    }

    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void signedit$keyPressed(
            net.minecraft.client.input.KeyEvent event,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (this.signedit$hexField != null
                && this.signedit$hexField.isFocused()) {

            if (this.signedit$hexField.keyPressed(
                    event
            )) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "charTyped",
            at = @At("HEAD"),
            cancellable = true
    )
    private void signedit$charTyped(
            net.minecraft.client.input.CharacterEvent event,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (this.signedit$hexField != null
                && this.signedit$hexField.isFocused()) {

            if (this.signedit$hexField.charTyped(
                    event
            )) {
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private boolean signedit$isMouseOverHexField(
            double mouseX,
            double mouseY
    ) {
        return mouseX >= this.signedit$hexField.getX()
                && mouseX < this.signedit$hexField.getX()
                + this.signedit$hexField.getWidth()
                && mouseY >= this.signedit$hexField.getY()
                && mouseY < this.signedit$hexField.getY()
                + this.signedit$hexField.getHeight();
    }

    @Override
    public void signedit$handleScreenMouseClick(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (this.signedit$hexField == null) {
            return;
        }

        if (!this.signedit$isMouseOverHexField(
                mouseX,
                mouseY
        )) {
            this.signedit$hexField.setFocused(false);
        }
    }

    @Unique
    public void signedit$finishScreenMouseClick() {
        if (!this.signedit$clearToolbarFocusAfterClick) {
            return;
        }

        ((ScreenInvoker) (Object) this).signedit$clearFocus();
        this.signedit$clearToolbarFocusAfterClick = false;
    }

    @Inject(method = "setMessage", at = @At("TAIL"))
    private void signedit$setMessage(String message, CallbackInfo ci) {
        this.signedit$applyPendingEdit(message);

        signedit$model.setLineText(
                this.line,
                message
        );

        this.text = this.text.setMessage(
                this.line,
                signedit$model.buildComponent(this.line)
        );

        this.sign.setText(
                this.text,
                this.isFrontText
        );
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void signedit$removed(CallbackInfo ci) {
        ClientPlayNetworking.send(
            SignFormattingPayloadFactory.create(
                this.sign.getBlockPos(),
                this.isFrontText,
                this.signedit$model
            )
        );
    }
}
