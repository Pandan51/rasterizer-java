package models.Shapes;

import models.Point;
import models.Polygon;
import java.awt.*;

/**
 * Model elipsy.
 */
public class Ellipse extends Polygon {
    private Point center;
    private int rx, ry;
    private boolean isPerfect = true;

    public Ellipse(Point center, int rx, int ry, Color color, Polygon.LineType type) {
        // Inicializujeme bez výplně v konstruktoru.
        super(color, null, type);
        this.center = center;
        this.rx = rx;
        this.ry = ry;
        this.setClosed(true);
        updateVertices();
    }

    public void updateVertices() {
        this.clearPoints();
        for (int i = 0; i < 40; i++) {
            double theta = 2.0 * Math.PI * i / 40;
            int x = (int) Math.round(center.getX() + rx * Math.cos(theta));
            int y = (int) Math.round(center.getY() + ry * Math.sin(theta));
            this.addPoint(new Point(x, y));
        }
    }

    public boolean isPerfect() { return isPerfect; }
    public void setPerfect(boolean perfect) { this.isPerfect = perfect; }
    public Point getCenter() { return center; }
    public int getRx() { return rx; }
    public int getRy() { return ry; }

    @Override
    public void move(int dx, int dy) {
        super.move(dx, dy);
        center.setX(center.getX() + dx);
        center.setY(center.getY() + dy);
    }
}