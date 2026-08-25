package de.lumax.signedit.access;

import de.lumax.signedit.text.SignTextModel;
import de.lumax.signedit.text.FormattingType;
import de.lumax.signedit.text.TextStyle;

public interface SignEditScreenAccess {

    int signedit$getCursorPos();

    int signedit$getSelectionPos();

    int signedit$getCurrentLine();

    boolean signedit$isSelecting();

    SignTextModel signedit$getModel();

    TextStyle signedit$getActiveStyle();

    void signedit$toggleFormatting(FormattingType type);

    void signedit$selectColor(int color);

    void signedit$clearToolbarFocusAfterClick();

    void signedit$setPendingEdit(
            int start,
            int end
    );
}
