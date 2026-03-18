package rasterizers;

import models.*;
import models.Point;
import models.Polygon;
import models.Shapes.Ellipse;
import java.util.ArrayList;

public class CanvasRasterizer {

    private final Rasterizer lineRasterizer;

    public CanvasRasterizer(Rasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(LineCanvas lineCanvas) {
        for (Polygon shape : lineCanvas.getShapes()) {
            lineRasterizer.setColor(shape.getColor());
            lineRasterizer.setThickness(shape.getThickness());

            // 1. Vector Fill (Scanline)
            if (shape.isFilled() && lineRasterizer instanceof TrivRasterizer) {
                ((TrivRasterizer) lineRasterizer).fillPolygon(shape);
            }

            // 2. Outline
            if (shape instanceof Ellipse && ((Ellipse) shape).isPerfect()) {
                lineRasterizer.rasterize((Ellipse) shape);
            } else {
                ArrayList<Point> points = shape.getPoints();
                if (points.size() < 2) continue;

                for (int i = 0; i < points.size() - 1; i++) {
                    drawEdge(points.get(i), points.get(i + 1), shape);
                }

                if (points.size() > 2 && shape.isClosed()) {
                    drawEdge(points.get(points.size() - 1), points.get(0), shape);
                }
            }
        }
    }

    private void drawEdge(Point a, Point b, Polygon shape) {
        // We create a temporary line object that carries the parent's lineType/thickness
        Line edge = new Line(a, b, shape.getColor(), shape.getLineType());
        edge.setThickness(shape.getThickness());
        lineRasterizer.rasterize(edge);
    }
}