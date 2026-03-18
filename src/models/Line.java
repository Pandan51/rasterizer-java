package models;

import java.awt.*;

public class Line extends Polygon {
    public Line(Point a, Point b, Color color, Polygon.LineType type) {
        super(color, type);
        this.addPoint(a);
        this.addPoint(b);
        this.setClosed(false);
    }

    public Point getA() { return points.get(0); }
    public Point getB() { return points.get(1); }
}