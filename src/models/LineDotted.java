package models;

import java.awt.*;

public class LineDotted extends Line{
    private final int gap;

    public LineDotted(Point a, Point b, Color color, int gap) {
        super(a, b, color);
        this.gap = gap;
    }

    public int getGap() { return gap;}
}
