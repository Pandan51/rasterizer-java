package rasterizers;


import models.*;
import models.Point;
import models.Polygon;

import java.awt.*;
import java.util.ArrayList;

import models.*;
import models.Point;
import models.Polygon;
import java.awt.*;
import java.util.ArrayList;

public class CanvasRasterizer {

    private final Rasterizer lineRasterizer;

    public CanvasRasterizer(Rasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(LineCanvas lineCanvas) {
        // Projdeme všechny tvary na plátně
        for (Polygon shape : lineCanvas.getShapes()) {
            ArrayList<Point> points = shape.getPoints();

            // Pro vykreslení čáry potřebujeme alespoň 2 body
            if (points.size() < 2) continue;

            // Nastavíme barvu rasterizéru podle barvy daného tvaru
            lineRasterizer.setColor(shape.getColor());

            // Propojíme body tvaru úsečkami
            for (int i = 0; i < points.size() - 1; i++) {
                drawEdge(points.get(i), points.get(i + 1), shape);
            }

            // Pokud je tvar uzavřený (např. obdélník, elipsa), spojíme poslední bod s prvním
            if (shape.isClosed() && points.size() > 2) {
                drawEdge(points.get(points.size() - 1), points.get(0), shape);
            }
        }
    }

    /**
     * Pomocná metoda pro vykreslení jedné hrany s ohledem na typ čáry
     */
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
