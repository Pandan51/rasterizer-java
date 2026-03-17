package models;

import java.awt.*;

import java.awt.*;

public class Line extends Polygon {
    public Line(Point a, Point b, Color color, boolean isDotted) {
        super(color, isDotted);
        this.addPoint(a);
        this.addPoint(b);
        this.setClosed(false); // Úsečka se nespojuje zpět na začátek
    }

    // Pomocné metody pro kompatibilitu se starším kódem
    public Point getA() { return points.get(0); }
    public Point getB() { return points.get(1); }
}
