package models;

import java.awt.*;

public class LineDotted extends Line {
    private final int gap;

    public LineDotted(Point a, Point b, Color color, int gap) {
        // Voláme konstruktor Line(Point a, Point b, Color color, boolean isDotted)
        super(a, b, color, true);
        this.gap = gap;
    }

    public int getGap() {
        return gap;
    }
}
