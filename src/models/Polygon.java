package models;

import java.awt.*;
import java.util.ArrayList;

public class Polygon {
    protected ArrayList<Point> points;
    private int lineType; // 0 = plná, 1 = tečkovaná
    private Color color;
    private boolean isClosed;
    private boolean isFilled;

    public Polygon(Color color, boolean isDotted) {
        this.points = new ArrayList<>();
        this.color = color;
        this.lineType = isDotted ? 1 : 0;
        this.isClosed = true; // Většina polygonů je uzavřená
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    public int getLineType() {
        return lineType;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public void clearPoints() {
        points.clear();
    }
}
