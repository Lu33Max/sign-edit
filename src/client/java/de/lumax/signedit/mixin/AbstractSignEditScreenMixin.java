package de.lumax.signedit.mixin;

import de.lumax.signedit.SignEditClient;
import de.lumax.signedit.access.SignEditHexFieldAccess;
import de.lumax.signedit.access.SignEditScreenAccess;
import de.lumax.signedit.access.TextFieldHelperAccess;
import de.lumax.signedit.config.AutoLineBreakMode;
import de.lumax.signedit.config.SignEditConfig;
import de.lumax.signedit.config.SignEditConfigScreen;
import de.lumax.signedit.gui.HexColorField;
import de.lumax.signedit.gui.SignColorPicker;
import de.lumax.signedit.gui.SignEditLayout;
import de.lumax.signedit.gui.SignEditSettingsButton;
import de.lumax.signedit.gui.SignFormattingToolbar;
import de.lumax.signedit.item.SignItemFactory;
import de.lumax.signedit.text.FormattingType;
import de.lumax.signedit.text.SignTextModel;
import de.lumax.signedit.text.TextStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin implements SignEditScreenAccess, SignEditHexFieldAccess {

    @Unique
    private final SignTextModel signedit$model = new SignTextModel();
    @Final
    @Shadow
    protected SignBlockEntity sign;
    @Shadow
    protected WoodType woodType;
    @Shadow
    private TextFieldHelper signField;
    @Shadow
    private int line;
    @Shadow
    private SignText text;
    @Unique
    private boolean signedit$transitioningScreen;
    @Mutable
    @Final
    @Shadow
    private boolean isFrontText;
    @Mutable
    @Final
    @Shadow
    private String[] messages;
    @Unique
    private boolean signedit$customScreen;

    @Unique
    private WoodType signedit$selectedWoodType;

    @Unique
    private WoodType signedit$initialWoodType;
    @Unique
    private boolean signedit$initialHangingSign;
    @Unique
    private TextStyle signedit$activeStyle = TextStyle.EMPTY;
    @Unique
    private boolean signedit$clearToolbarFocusAfterClick;
    @Unique
    private int signedit$lastCursorPos = -1;
    @Unique
    private int signedit$lastLine = -1;
    @Unique
    private SignColorPicker signedit$colorPicker;
    @Unique
    private HexColorField signedit$hexField;
    @Unique
    private SignFormattingToolbar signedit$toolbar;
    @Unique
    private SignText signedit$initialFrontText;
    @Unique
    private SignText signedit$initialBackText;
    @Unique
    private SignText signedit$formInitialFrontText;
    @Unique
    private SignText signedit$formInitialBackText;
    @Unique
    private SignText signedit$wideFrontText;
    @Unique
    private SignText signedit$wideBackText;
    @Unique
    private int signedit$lineBeforeCharacterInput;
    @Unique
    private String signedit$messageBeforeCharacterInput;

    @Override
    public boolean signedit$isCustomScreen() {
        return this.signedit$customScreen;
    }

    @Override
    public void signedit$setCustomScreen(boolean customScreen) {
        this.signedit$customScreen = customScreen;
    }

    @Override
    public WoodType signedit$getWoodType() {
        return this.signedit$selectedWoodType;
    }

    @Override
    public void signedit$cycleWoodType(int direction) {
        WoodType[] types = SignItemFactory.availableWoodTypes(
                this.sign instanceof HangingSignBlockEntity
        ).toArray(WoodType[]::new);
        int current = 0;

        for (int index = 0; index < types.length; index++) {
            if (types[index] == this.signedit$selectedWoodType) {
                current = index;
                break;
            }
        }

        int next = Math.floorMod(current + direction, types.length);
        this.signedit$selectedWoodType = types[next];
    }

    @Override
    public boolean signedit$isFrontText() {
        return this.isFrontText;
    }

    @Override
    public void signedit$toggleTextSide() {
        this.sign.setText(this.signedit$model.buildSignText(), this.isFrontText);
        this.isFrontText = !this.isFrontText;
        this.text = this.sign.getText(this.isFrontText);

        for (int line = 0; line < this.messages.length; line++) {
            this.messages[line] = this.text.getMessage(line, false).getString();
        }

        this.signedit$model.loadFromSignText(this.text, false);
        this.line = 0;
        this.signField.setCursorToEnd();
        this.signedit$activeStyle = TextStyle.EMPTY;
        this.signedit$updateToolbar();
    }

    @Override
    public boolean signedit$isHangingSign() {
        return this.sign instanceof HangingSignBlockEntity;
    }

    @Override
    public void signedit$setSessionState(
            SignText initialFrontText,
            SignText initialBackText,
            WoodType initialWoodType,
            boolean initialHangingSign,
            SignText wideFrontText,
            SignText wideBackText
    ) {
        this.signedit$initialFrontText = initialFrontText;
        this.signedit$initialBackText = initialBackText;
        this.signedit$initialWoodType = initialWoodType;
        this.signedit$initialHangingSign = initialHangingSign;
        this.signedit$wideFrontText = wideFrontText;
        this.signedit$wideBackText = wideBackText;
    }

    @Override
    public void signedit$toggleSignType() {
        this.sign.setText(this.signedit$model.buildSignText(), this.isFrontText);

        boolean targetHanging = !this.signedit$isHangingSign();
        SignText currentFrontText = this.sign.getFrontText();
        SignText currentBackText = this.sign.getBackText();
        SignText wideFrontText = targetHanging
                ? currentFrontText
                : this.signedit$wideFrontText;
        SignText wideBackText = targetHanging
                ? currentBackText
                : this.signedit$wideBackText;
        SignText targetFrontText = targetHanging
                ? currentFrontText
                : signedit$restoreWideLines(
                currentFrontText,
                this.signedit$formInitialFrontText,
                wideFrontText
        );
        SignText targetBackText = targetHanging
                ? currentBackText
                : signedit$restoreWideLines(
                currentBackText,
                this.signedit$formInitialBackText,
                wideBackText
        );

        Block targetBlock = SignItemFactory.getSignBlock(
                this.signedit$selectedWoodType,
                targetHanging
        );
        if (targetBlock == null) {
            return;
        }

        BlockState targetState = targetBlock.defaultBlockState();
        SignBlockEntity targetSign = targetHanging
                ? new HangingSignBlockEntity(this.sign.getBlockPos(), targetState)
                : new SignBlockEntity(this.sign.getBlockPos(), targetState);
        targetSign.setLevel(this.sign.getLevel());
        targetSign.setText(targetFrontText, true);
        targetSign.setText(targetBackText, false);

        this.signedit$transitioningScreen = true;
        SignEditClient.openEditor(
                ((ScreenInvoker) this).signedit$getMinecraft(),
                targetSign,
                this.isFrontText,
                this.signedit$initialFrontText,
                this.signedit$initialBackText,
                this.signedit$initialWoodType,
                this.signedit$initialHangingSign,
                wideFrontText,
                wideBackText
        );
    }

    @Unique
    private SignText signedit$restoreWideLines(
            SignText currentText,
            SignText formInitialText,
            SignText wideText
    ) {
        SignText result = currentText;

        for (int line = 0; line < SignText.LINES; line++) {
            if (signedit$lineEquals(currentText, formInitialText, line)) {
                result = result.setMessage(
                        line,
                        wideText.getMessage(line, false),
                        wideText.getMessage(line, true)
                );
            }
        }

        return result;
    }

    @Unique
    private boolean signedit$lineEquals(SignText first, SignText second, int line) {
        return first.getMessage(line, false).equals(second.getMessage(line, false))
                && first.getMessage(line, true).equals(second.getMessage(line, true));
    }

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
    public void signedit$resetColor() {
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
                ((ScreenInvoker) this).signedit$getMinecraft();

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
        if (!this.signedit$customScreen) {
            return x;
        }

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
        if (!this.signedit$customScreen) {
            return x;
        }

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
        if (!this.signedit$customScreen) {
            graphics.text(font, str, x, y, color, dropShadow);
            return;
        }

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
        if (!this.signedit$customScreen) {
            return font.width(text);
        }

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
        if (!this.signedit$customScreen) {
            graphics.textHighlight(fromX, fromY, toX, toY, render);
            return;
        }

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
                ((ScreenInvoker) this).signedit$getMinecraft();

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

    @Inject(method = "init", at = @At("TAIL"))
    private void signedit$init(CallbackInfo ci) {
        AbstractSignEditScreen screen = (AbstractSignEditScreen) (Object) this;
        ((ScreenInvoker) this).signedit$addRenderableWidget(new SignEditSettingsButton(
                4,
                4,
                () -> ((ScreenInvoker) this).signedit$getMinecraft().gui.setScreen(
                        new SignEditConfigScreen(screen)
                )
        ));

        if (!this.signedit$customScreen) {
            return;
        }

        this.signedit$model.loadFromSignText(
                this.text,
                false
        );
        this.signedit$initialFrontText = signedit$normalizeText(
                this.sign.getFrontText()
        );
        this.signedit$initialBackText = signedit$normalizeText(
                this.sign.getBackText()
        );
        this.signedit$formInitialFrontText = this.signedit$initialFrontText;
        this.signedit$formInitialBackText = this.signedit$initialBackText;
        this.signedit$wideFrontText = this.signedit$initialFrontText;
        this.signedit$wideBackText = this.signedit$initialBackText;
        this.signedit$selectedWoodType = this.woodType;
        this.signedit$initialWoodType = this.woodType;
        this.signedit$initialHangingSign = this.signedit$isHangingSign();

        SignEditLayout signedit$layout = SignEditLayout.addTo(
                screen,
                this,
                this::signedit$resetColor
        );
        this.signedit$colorPicker = signedit$layout.getColorPicker();
        this.signedit$hexField = signedit$layout.getHexField();
        this.signedit$toolbar = signedit$layout.getToolbar();
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

        ((ScreenInvoker) this).signedit$clearFocus();
        this.signedit$clearToolbarFocusAfterClick = false;
    }

    @Inject(method = "setMessage", at = @At("TAIL"))
    private void signedit$setMessage(String message, CallbackInfo ci) {
        if (!this.signedit$customScreen) {
            return;
        }

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

    @Inject(method = "charTyped", at = @At("HEAD"))
    private void signedit$recordCharacterInput(
            net.minecraft.client.input.CharacterEvent event,
            CallbackInfoReturnable<Boolean> cir
    ) {
        this.signedit$lineBeforeCharacterInput = this.line;
        this.signedit$messageBeforeCharacterInput = this.messages[this.line];
    }

    @Inject(method = "charTyped", at = @At("TAIL"))
    private void signedit$autoLineBreak(
            net.minecraft.client.input.CharacterEvent event,
            CallbackInfoReturnable<Boolean> cir
    ) {
        boolean isLastLine = this.signedit$lineBeforeCharacterInput
                >= this.messages.length - 1;
        boolean canAdvance = !isLastLine
                || SignEditConfig.isWrapToFirstLineEnabled();

        if (SignEditConfig.getAutoLineBreakMode() == AutoLineBreakMode.OFF
                || !event.isAllowedChatCharacter()
                || !canAdvance
                || this.line != this.signedit$lineBeforeCharacterInput
                || !this.messages[this.line].equals(
                this.signedit$messageBeforeCharacterInput
        )) {
            return;
        }

        signedit$advanceLine(this.signedit$messageBeforeCharacterInput);
        this.signField.insertText(event.codepointAsString());
    }

    @Unique
    private void signedit$advanceLine(String currentLine) {
        int nextLine = (this.line + 1) % this.messages.length;
        int cursorPosition = 0;

        if (SignEditConfig.getAutoLineBreakMode()
                == AutoLineBreakMode.MOVE_WORD_TO_NEXT_LINE) {
            int wordStart = signedit$findCurrentWordStart(currentLine);

            if (wordStart > 0) {
                String movedWord = currentLine.substring(wordStart);
                this.messages[this.line] = currentLine.substring(0, wordStart);
                this.messages[nextLine] = movedWord + this.messages[nextLine];

                if (this.signedit$customScreen) {
                    this.signedit$model.moveLineSuffix(
                            this.line,
                            wordStart,
                            nextLine
                    );
                    this.text = this.text.setMessage(
                            this.line,
                            this.signedit$model.buildComponent(this.line)
                    ).setMessage(
                            nextLine,
                            this.signedit$model.buildComponent(nextLine)
                    );
                    this.sign.setText(this.text, this.isFrontText);
                }

                cursorPosition = movedWord.length();
            }
        }

        this.line = nextLine;
        this.signField.setCursorPos(cursorPosition, false);
        this.signedit$lastCursorPos = -1;
        this.signedit$lastLine = -1;
    }

    @Unique
    private int signedit$findCurrentWordStart(String text) {
        for (int index = text.length() - 1; index >= 0; index--) {
            if (Character.isWhitespace(text.charAt(index))) {
                return index + 1;
            }
        }

        return 0;
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void signedit$removed(CallbackInfo ci) {
        if (!this.signedit$customScreen) {
            return;
        }

        if (this.signedit$transitioningScreen) {
            return;
        }

        Minecraft minecraft = ((ScreenInvoker) this).signedit$getMinecraft();

        if (minecraft.player == null || minecraft.getConnection() == null) {
            return;
        }

        SignText editedText = this.signedit$model.buildSignText();
        SignText finalFrontText = this.isFrontText
                ? editedText
                : this.sign.getFrontText();
        SignText finalBackText = this.isFrontText
                ? this.sign.getBackText()
                : editedText;

        if (signedit$isEmpty(finalFrontText, finalBackText)
                || (signedit$textEquals(this.signedit$initialFrontText, finalFrontText)
                && signedit$textEquals(this.signedit$initialBackText, finalBackText)
                && this.signedit$initialWoodType == this.signedit$selectedWoodType
                && this.signedit$initialHangingSign == this.signedit$isHangingSign())) {
            return;
        }

        SignText originalOtherSide = this.sign.getText(!this.isFrontText);
        SignText frontText = this.isFrontText
                ? editedText
                : originalOtherSide;
        SignText backText = this.isFrontText
                ? originalOtherSide
                : editedText;

        var item = SignItemFactory.create(
                this.signedit$selectedWoodType,
                this.sign instanceof HangingSignBlockEntity,
                frontText,
                backText
        );

        int slot = signedit$getInventorySlot(minecraft);

        minecraft.player.getInventory().setItem(slot, item);
        int menuSlot = slot < 9 ? slot + 36 : slot;

        minecraft.execute(() -> {
            if (minecraft.getConnection() != null) {
                minecraft.getConnection().send(
                        new ServerboundSetCreativeModeSlotPacket(menuSlot, item)
                );
            }
        });
        minecraft.textInputManager().stopTextInput();
    }

    @Unique
    private int signedit$getInventorySlot(Minecraft minecraft) {
        int selectedSlot = minecraft.player.getInventory().getSelectedSlot();

        if (minecraft.player.getInventory().getItem(selectedSlot).isEmpty()) {
            return selectedSlot;
        }

        for (int slot = 0; slot < 9; slot++) {
            if (slot != selectedSlot
                    && minecraft.player.getInventory().getItem(slot).isEmpty()) {
                return slot;
            }
        }

        return selectedSlot;
    }

    @Unique
    private boolean signedit$isEmpty(SignText frontText, SignText backText) {
        for (int line = 0; line < 4; line++) {
            if (!frontText.getMessage(line, false).getString().isEmpty()
                    || !backText.getMessage(line, false).getString().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Unique
    private boolean signedit$textEquals(SignText first, SignText second) {
        return SignText.DIRECT_CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                first
        ).getOrThrow().equals(
                SignText.DIRECT_CODEC.encodeStart(
                        net.minecraft.nbt.NbtOps.INSTANCE,
                        second
                ).getOrThrow()
        );
    }

    @Unique
    private SignText signedit$normalizeText(SignText text) {
        SignTextModel model = new SignTextModel();
        model.loadFromSignText(text, false);
        return model.buildSignText();
    }
}
