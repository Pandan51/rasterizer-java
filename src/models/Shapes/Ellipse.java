package models.Shapes;

import models.Point;
import models.Polygon;
import java.awt.*;

public class Ellipse extends Polygon {
    public Ellipse(Point center, int rx, int ry, Color color, boolean isDotted) {
        super(color, isDotted);
        generatePoints(center, rx, ry, 64); // 64 segmentů pro hladký vzhled
        this.setClosed(true);
    }

    private void generatePoints(Point center, int rx, int ry, int segments) {
        for (int i = 0; i < segments; i++) {
            double theta = 2.0 * Math.PI * i / segments;
            int x = (int) Math.round(center.getX() + rx * Math.cos(theta));
            int y = (int) Math.round(center.getY() + ry * Math.sin(theta));
            this.addPoint(new Point(x, y));
        }
    }
}