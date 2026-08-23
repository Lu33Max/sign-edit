package de.lumax.client.mixin;

import de.lumax.signedit.access.TextFieldHelperAccess;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(TextFieldHelper.class)
public abstract class TextFieldHelperMixin
        implements TextFieldHelperAccess {

    @Shadow
    private Supplier<String> getMessageFn;

    @Unique
    private int signedit$editStart;

    @Unique
    private int signedit$editEnd;

    @Unique
    private boolean signedit$hasPendingEdit;

    @Unique
    private String signedit$getMessage() {
        return this.getMessageFn.get();
    }

    @Override
    public int signedit$getEditStart() {
        return this.signedit$editStart;
    }

    @Override
    public int signedit$getEditEnd() {
        return this.signedit$editEnd;
    }

    @Override
    public boolean signedit$hasPendingEdit() {
        return this.signedit$hasPendingEdit;
    }

    @Override
    public void signedit$clearPendingEdit() {
        this.signedit$hasPendingEdit = false;
    }

    @Inject(
            method = "insertText",
            at = @At("HEAD")
    )
    private void signedit$beforeInsert(
            String message,
            String text,
            CallbackInfo ci
    ) {
        TextFieldHelper self =
                (TextFieldHelper) (Object) this;

        int cursor = self.getCursorPos();
        int selection = self.getSelectionPos();

        this.signedit$editStart =
                Math.min(cursor, selection);

        this.signedit$editEnd =
                Math.max(cursor, selection);

        this.signedit$hasPendingEdit = true;
    }

    @Inject(
            method = "removeCharsFromCursor",
            at = @At("HEAD")
    )
    private void signedit$beforeRemoveChars(
            int count,
            CallbackInfo ci
    ) {
        TextFieldHelper self =
                (TextFieldHelper) (Object) this;

        int cursor = self.getCursorPos();
        int selection = self.getSelectionPos();

        if (cursor != selection) {
            // Eine Selection wird gelöscht
            this.signedit$editStart =
                    Math.min(cursor, selection);

            this.signedit$editEnd =
                    Math.max(cursor, selection);

            this.signedit$hasPendingEdit = true;
            return;
        }

        /*
         * Keine Selection:
         * Vanilla berechnet weiter unten:
         *
         * int otherPos = Util.offsetByCodepoints(
         *     message,
         *     this.cursorPos,
         *     count
         * );
         *
         * int start = Math.min(otherPos, this.cursorPos);
         * int end = Math.max(otherPos, this.cursorPos);
         *
         * Wir müssen exakt dasselbe machen.
         */
        String message = this.signedit$getMessage();

        int otherPos = Util.offsetByCodepoints(
                message,
                cursor,
                count
        );

        this.signedit$editStart =
                Math.min(otherPos, cursor);

        this.signedit$editEnd =
                Math.max(otherPos, cursor);

        this.signedit$hasPendingEdit = true;
    }
}
