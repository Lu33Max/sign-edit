package de.lumax.signedit.text;

import java.util.ArrayList;
import java.util.List;

public class StyledLine {

    private String text;

    private final List<StyleRange> ranges = new ArrayList<>();

    public StyledLine() {
        this("");
    }

    public StyledLine(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<StyleRange> getRanges() {
        return ranges;
    }

    public void clearStyles() {
        ranges.clear();
    }

    public void addRange(StyleRange range) {
        ranges.add(range);
    }

    public void toggleFormatting(
            int start,
            int end,
            FormattingType type
    ) {
        if (start < 0 || end > text.length() || start >= end) {
            return;
        }

        boolean allEnabled = true;

        for (int i = start; i < end; i++) {
            if (!getStyleAt(i).withFormatting(type, true).equals(getStyleAt(i))) {
                allEnabled = false;
                break;
            }
        }

        boolean newValue = !allEnabled;

        List<StyleRange> newRanges = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            TextStyle style = getStyleAt(i);

            if (i >= start && i < end) {
                style = style.withFormatting(type, newValue);
            }

            if (style.equals(TextStyle.EMPTY)) {
                continue;
            }

            if (!newRanges.isEmpty()) {
                StyleRange previous = newRanges.getLast();

                if (previous.end() == i
                        && previous.style().equals(style)) {

                    newRanges.set(
                            newRanges.size() - 1,
                            new StyleRange(
                                    previous.start(),
                                    i + 1,
                                    style
                            )
                    );

                    continue;
                }
            }

            newRanges.add(
                    new StyleRange(
                            i,
                            i + 1,
                            style
                    )
            );
        }

        ranges.clear();
        ranges.addAll(newRanges);
    }

    public void setFormatting(
            int start,
            int end,
            FormattingType type,
            boolean value
    ) {
        if (start < 0 || end > text.length() || start >= end) {
            return;
        }

        List<TextStyle> styles = new ArrayList<>(text.length());

        for (int i = 0; i < text.length(); i++) {
            TextStyle style = getStyleAt(i);
            styles.add(i >= start && i < end
                    ? style.withFormatting(type, value)
                    : style);
        }

        rebuildRanges(styles);
    }

    public void replaceText(
            String newText,
            int editStart,
            int editEnd
    ) {
        replaceText(newText, editStart, editEnd, null);
    }

    public void replaceText(
            String newText,
            int editStart,
            int editEnd,
            TextStyle requestedInsertedStyle
    ) {
        String oldText = this.text;

        editStart = Math.max(0, Math.min(editStart, oldText.length()));
        editEnd = Math.max(editStart, Math.min(editEnd, oldText.length()));

        List<TextStyle> oldStyles = new ArrayList<>(oldText.length());

        for (int i = 0; i < oldText.length(); i++) {
            oldStyles.add(getStyleAt(i));
        }

        List<TextStyle> newStyles = new ArrayList<>(newText.length());

        // Linken unveränderten Bereich übernehmen
        for (int i = 0; i < editStart; i++) {
            newStyles.add(oldStyles.get(i));
        }

        // Style für neu eingefügten Text
        TextStyle insertedStyle = requestedInsertedStyle;

        if (insertedStyle == null && editStart > 0) {
            insertedStyle = oldStyles.get(editStart - 1);
        } else if (insertedStyle == null && editEnd < oldStyles.size()) {
            insertedStyle = oldStyles.get(editEnd);
        } else if (insertedStyle == null) {
            insertedStyle = TextStyle.EMPTY;
        }

        int insertedLength =
                newText.length()
                        - (oldText.length() - (editEnd - editStart));

        for (int i = 0; i < insertedLength; i++) {
            newStyles.add(insertedStyle);
        }

        // Rechten unveränderten Bereich übernehmen
        for (int oldIndex = editEnd;
             oldIndex < oldText.length();
             oldIndex++) {

            newStyles.add(oldStyles.get(oldIndex));
        }

        this.text = newText;

        rebuildRanges(newStyles);
    }

    public TextStyle getStyleAt(int index) {
        TextStyle result = TextStyle.EMPTY;

        for (StyleRange range : ranges) {
            if (range.contains(index)) {
                result = merge(result, range.style());
            }
        }

        return result;
    }

    public Integer getColorAt(int index) {
        if (index < 0 || index >= text.length()) {
            return null;
        }

        return getStyleAt(index).color();
    }

    public void setColor(
            int start,
            int end,
            int color
    ) {
        if (start < 0 || end > text.length() || start >= end) {
            return;
        }

        List<StyleRange> newRanges = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            TextStyle style = getStyleAt(i);

            if (i >= start && i < end) {
                style = style.withColor(color);
            }

            if (style.equals(TextStyle.EMPTY)) {
                continue;
            }

            if (!newRanges.isEmpty()) {
                StyleRange previous =
                        newRanges.getLast();

                if (previous.end() == i
                        && previous.style().equals(style)) {

                    newRanges.set(
                            newRanges.size() - 1,
                            new StyleRange(
                                    previous.start(),
                                    i + 1,
                                    style
                            )
                    );

                    continue;
                }
            }

            newRanges.add(
                    new StyleRange(
                            i,
                            i + 1,
                            style
                    )
            );
        }

        ranges.clear();
        ranges.addAll(newRanges);
    }

    private TextStyle merge(TextStyle base, TextStyle overlay) {
        return new TextStyle(
                base.bold() || overlay.bold(),
                base.italic() || overlay.italic(),
                base.underlined() || overlay.underlined(),
                base.strikethrough() || overlay.strikethrough(),
                base.obfuscated() || overlay.obfuscated(),
                overlay.color() != null
                        ? overlay.color()
                        : base.color()
        );
    }

    private void rebuildRanges(
            List<TextStyle> styles
    ) {
        ranges.clear();

        if (styles.isEmpty()) {
            return;
        }

        int rangeStart = 0;
        TextStyle currentStyle = styles.getFirst();

        for (int i = 1; i < styles.size(); i++) {
            TextStyle style = styles.get(i);

            if (!style.equals(currentStyle)) {
                if (!currentStyle.equals(TextStyle.EMPTY)) {
                    ranges.add(
                            new StyleRange(
                                    rangeStart,
                                    i,
                                    currentStyle
                            )
                    );
                }

                rangeStart = i;
                currentStyle = style;
            }
        }

        if (!currentStyle.equals(TextStyle.EMPTY)) {
            ranges.add(
                    new StyleRange(
                            rangeStart,
                            styles.size(),
                            currentStyle
                    )
            );
        }
    }
}
