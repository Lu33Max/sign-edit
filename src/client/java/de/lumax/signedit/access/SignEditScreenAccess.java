package de.lumax.signedit.access;

import de.lumax.signedit.text.SignTextModel;

public interface SignEditScreenAccess {

    int signedit$getCursorPos();

    int signedit$getSelectionPos();

    int signedit$getCurrentLine();

    boolean signedit$isSelecting();

    SignTextModel signedit$getModel();

    void signedit$setPendingEdit(
            int start,
            int end
    );
}
