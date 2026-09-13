package de.lumax.signedit.access;

import de.lumax.signedit.text.FormattingType;
import de.lumax.signedit.text.TextStyle;

public interface SignEditScreenAccess {

    TextStyle signedit$getActiveStyle();

    void signedit$toggleFormatting(FormattingType type);

    void signedit$selectColor(int color);

    void signedit$clearToolbarFocusAfterClick();
}
