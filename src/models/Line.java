package models;

import java.awt.*;

public class Line {
    Point a,  b;
    Color color;

    public Line(Point a, Point b, Color color) {
        this.a = a;
        this.b = b;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Point getB() {
        return b;
    }

    public Point getA() {
        return a;
    }
}
