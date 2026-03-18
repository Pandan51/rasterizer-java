package models;

import java.awt.*;
import java.util.ArrayList;

public class Polygon {
    // Consolidated enum to LineType
    public enum LineType { SOLID, DOTTED, DASHED }

    protected ArrayList<Point> points;
    private LineType lineType = LineType.SOLID;
    private Color color;
    private int thickness = 1;
    private boolean isClosed;
    private boolean isFilled;

    public Polygon(Color color, LineType type) {
        this.points = new ArrayList<>();
        this.color = color;
        this.lineType = type;
        this.isClosed = true;
    }

    public void addPoint(Point point) { points.add(point); }
    public ArrayList<Point> getPoints() { return points; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public int getThickness() { return thickness; }
    public void setThickness(int thickness) { this.thickness = thickness; }

    public LineType getLineType() { return lineType; }
    public void setLineType(LineType type) { this.lineType = type; }

    public boolean isClosed() { return isClosed; }
    public void setClosed(boolean closed) { isClosed = closed; }
    public boolean isFilled() { return isFilled; }
    public void setFilled(boolean filled) { isFilled = filled; }
    public void clearPoints() { points.clear(); }

    public void move(int dx, int dy) {
        for (Point p : points) {
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
    }

    /**
     * Point-in-Polygon test (Even-Odd rule) for hit-testing.
     */
    public boolean contains(int x, int y) {
        if (points.size() < 3) return false;
        boolean inside = false;
        for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if (((points.get(i).getY() > y) != (points.get(j).getY() > y)) &&
                    (x < (points.get(j).getX() - points.get(i).getX()) * (y - points.get(i).getY()) /
                            (points.get(j).getY() - points.get(i).getY()) + points.get(i).getX())) {
                inside = !inside;
            }
        }
        return inside;
    }
}