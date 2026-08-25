package de.lumax.signedit.access;

public interface SignEditHexFieldAccess {

    void signedit$handleScreenMouseClick(
            double mouseX,
            double mouseY,
            int button
    );

    void signedit$finishScreenMouseClick();
}
