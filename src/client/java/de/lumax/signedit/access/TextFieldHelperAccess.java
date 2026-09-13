package de.lumax.signedit.access;

public interface TextFieldHelperAccess {
    int signedit$getEditStart();

    int signedit$getEditEnd();

    boolean signedit$hasPendingEdit();

    void signedit$clearPendingEdit();
}
