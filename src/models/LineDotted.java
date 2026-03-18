package models;

import java.awt.*;

/**
 * Model tečkované úsečky.
 */
public class LineDotted extends Line {
    private final int gap;

    public LineDotted(Point a, Point b, Color color, int gap) {
        // Voláme konstruktor Line, který už správně deleguje do Polygon
        super(a, b, color, Polygon.LineType.DOTTED);
        this.gap = gap;
    }

    public int getGap() {
        return gap;
    }
}