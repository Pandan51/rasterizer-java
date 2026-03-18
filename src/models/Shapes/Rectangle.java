package models.Shapes;

import models.Point;
import models.Polygon;
import java.awt.*;

public class Rectangle extends Polygon {
    public Rectangle(Point a, Point b, Color color, Polygon.LineType type) {
        super(color, type);
        this.addPoint(new Point(a.getX(), a.getY()));
        this.addPoint(new Point(b.getX(), a.getY()));
        this.addPoint(new Point(b.getX(), b.getY()));
        this.addPoint(new Point(a.getX(), b.getY()));
        this.setClosed(true);
    }
}