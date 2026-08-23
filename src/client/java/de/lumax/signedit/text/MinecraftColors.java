package de.lumax.signedit.text;

public final class MinecraftColors {

    private MinecraftColors() {
    }

    public record ColorEntry(
            String name,
            int rgb
    ) {
    }

    public static final ColorEntry[] COLORS = {
            new ColorEntry("White", 0x666666),
            new ColorEntry("Light Gray", 0x555555),
            new ColorEntry("Gray", 0x333333),
            new ColorEntry("Black", 0x000000),

            new ColorEntry("Brown", 0x321500),
            new ColorEntry("Red", 0x640000),
            new ColorEntry("Orange", 0x663300),
            new ColorEntry("Yellow", 0x666600),

            new ColorEntry("Lime", 0x4c6600),
            new ColorEntry("Green", 0x006600),
            new ColorEntry("Cyan", 0x006666),
            new ColorEntry("Light Blue", 0x3a4a50),

            new ColorEntry("Blue", 0x000066),
            new ColorEntry("Purple", 0x400d61),
            new ColorEntry("Magenta", 0x660066),
            new ColorEntry("Pink", 0x642645),
    };
}