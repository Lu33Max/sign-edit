package de.lumax.signedit.access;

import de.lumax.signedit.text.FormattingType;
import de.lumax.signedit.text.TextStyle;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.entity.SignText;

public interface SignEditScreenAccess {

    boolean signedit$isCustomScreen();

    void signedit$setCustomScreen(boolean customScreen);

    WoodType signedit$getWoodType();

    void signedit$cycleWoodType(int direction);

    boolean signedit$isFrontText();

    void signedit$toggleTextSide();

        boolean signedit$isHangingSign();

        void signedit$toggleSignType();

        void signedit$setSessionState(
            SignText initialFrontText,
            SignText initialBackText,
            WoodType initialWoodType,
            SignText wideFrontText,
            SignText wideBackText
        );

    TextStyle signedit$getActiveStyle();

    void signedit$toggleFormatting(FormattingType type);

    void signedit$selectColor(int color);

    void signedit$clearToolbarFocusAfterClick();
}
