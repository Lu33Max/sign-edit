package de.lumax.signedit.text;

public record StyleRange(
        int start,
        int end,
        TextStyle style
) {
    public boolean contains(int index) {
        return index >= start && index < end;
    }
}
