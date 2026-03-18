package models.Shapes;

import models.Point;
import models.Polygon;
import java.awt.*;

public class Ellipse extends Polygon {
    private Point center;
    private int rx, ry;
    private boolean isPerfect = true; // Pokud je true, kreslí se Bresenhamem


    public Ellipse(Point center, int rx, int ry, Color color, boolean isDotted) {
        super(color, isDotted);
        this.center = center;
        this.rx = rx;
        this.ry = ry;
        this.setClosed(true);

        // Vygenerování bodů pro kompatibilitu s polygonovými operacemi (např. hit-testing)
        updateVertices();
    }

    public void updateVertices() {
        this.clearPoints();
        // Generujeme 40 bodů pro vizuální aproximaci v seznamech
        for (int i = 0; i < 40; i++) {
            double theta = 2.0 * Math.PI * i / 40;
            int x = (int) Math.round(center.getX() + rx * Math.cos(theta));
            int y = (int) Math.round(center.getY() + ry * Math.sin(theta));
            this.addPoint(new Point(x, y));
        }
    }

    public boolean isPerfect() { return isPerfect; }
    public Point getCenter() { return center; }
    public int getRx() { return rx; }
    public int getRy() { return ry; }
}