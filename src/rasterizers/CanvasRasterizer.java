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

            // Pokud jde o elipsu, použijeme přesný algoritmus (Bresenham)
            if (shape instanceof Ellipse) {
                lineRasterizer.rasterize((Ellipse) shape);
            } else {
                ArrayList<Point> points = shape.getPoints();
                if (points.size() < 2) continue;

                // Vykreslení spojnic mezi body (1-2, 2-3, ...)
                for (int i = 0; i < points.size() - 1; i++) {
                    drawEdge(points.get(i), points.get(i + 1), shape);
                }

                // Zjednodušená podmínka: Pokud máme více než 2 body,
                // polygon se automaticky uzavře spojením posledního s prvním.
                if (points.size() > 2) {
                    drawEdge(points.get(points.size() - 1), points.get(0), shape);
                }
            }
        }
    }

    private void drawEdge(Point a, Point b, Polygon shape) {
        Line edge;
        if (shape.getLineType() == 1) { // Tečkovaná
            edge = new LineDotted(a, b, shape.getColor(), 5);
        } else { // Plná
            edge = new Line(a, b, shape.getColor(), false);
        }
        lineRasterizer.rasterize(edge);
    }
}