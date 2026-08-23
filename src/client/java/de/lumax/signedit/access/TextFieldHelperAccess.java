package de.lumax.signedit.access;

import java.util.function.BiConsumer;

public interface TextFieldHelperAccess {
    int signedit$getEditStart();

    int signedit$getEditEnd();

    boolean signedit$hasPendingEdit();

    void signedit$clearPendingEdit();
}
