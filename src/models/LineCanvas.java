package models;

import java.util.ArrayList;

public class LineCanvas {
    // Sjednocený seznam pro všechny tvary (Line, Rectangle, Ellipse, Polygon)
    private final ArrayList<Polygon> shapes;

    public LineCanvas() {
        shapes = new ArrayList<>();
    }

    public void addShape(Polygon shape) {
        shapes.add(shape);
    }

    public void clear() {
        shapes.clear();
    }

    public ArrayList<Polygon> getShapes() {
        return shapes;
    }
}