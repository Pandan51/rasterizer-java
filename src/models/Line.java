package models;

import java.awt.*;

/**
 * Model úsečky.
 */
public class Line extends Polygon {
    public Line(Point a, Point b, Color color, Polygon.LineType type) {
        // Předáváme null pro fillColor, protože úsečka se nevyplňuje.
        // Výplň lze nastavit později přes setFillColor.
        super(color, null, type);
        this.addPoint(a);
        this.addPoint(b);
        this.setClosed(false);
    }

    public Point getA() { return points.get(0); }
    public Point getB() { return points.get(1); }
}