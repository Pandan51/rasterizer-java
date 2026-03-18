package rasterizers;

import models.*;
import models.Point;
import models.Polygon;
import models.Shapes.Ellipse;
import java.util.ArrayList;
import java.awt.Color;

public class CanvasRasterizer {

    private final Rasterizer lineRasterizer;

    /**
     * Vytvoří koordinátora vykreslování nad konkrétním rasterizérem.
     */
    public CanvasRasterizer(Rasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    /**
     * Projde seznam tvarů v plátně a vykreslí je. Dodržuje pořadí: výplň, obrys a případně modré úchopy výběru.
     */
    public void rasterize(LineCanvas lineCanvas, Polygon selectedPolygon) {
        for (Polygon shape : lineCanvas.getShapes()) {
            lineRasterizer.setThickness(shape.getThickness());

            // 1. Výplň
            if (shape.isFilled() && lineRasterizer instanceof TrivRasterizer) {
                lineRasterizer.setColor(shape.getFillColor());
                ((TrivRasterizer) lineRasterizer).fillPolygon(shape);
            }

            // 2. Obrys
            lineRasterizer.setColor(shape.getColor());
            if (shape instanceof Ellipse && ((Ellipse) shape).isPerfect()) {
                lineRasterizer.rasterize((Ellipse) shape);
            } else {
                ArrayList<Point> points = shape.getPoints();
                if (points.size() < 2) continue;

                for (int i = 0; i < points.size() - 1; i++) {
                    drawEdge(points.get(i), points.get(i + 1), shape);
                }

                if (shape.isClosed() && points.size() > 2) {
                    drawEdge(points.get(points.size() - 1), points.get(0), shape);
                }
            }

            // 3. Zvýraznění (Tečky / Úchopy) - vykreslíme pouze pro vybraný objekt
            if (shape == selectedPolygon) {
                drawSelectionHandles(shape);
            }
        }
    }

    /**
     * Vykreslí malé čtverečky (úchopy) v každém vrcholu právě vybraného polygonu pro vizuální odezvu.
     */
    private void drawSelectionHandles(Polygon shape) {
        lineRasterizer.setColor(Color.BLUE); // Úchopy budou modré
        lineRasterizer.setThickness(1);

        for (Point p : shape.getPoints()) {
            // Vykreslíme čtvereček 5x5 pixelů centrovaný na bodě
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    // Přímý přístup k triviálnímu rasterizéru pro vykreslení jednotlivých bodů
                    if (lineRasterizer instanceof TrivRasterizer) {
                        ((TrivRasterizer) lineRasterizer).rasterizePoint(p.getX() + dx, p.getY() + dy);
                    }
                }
            }
        }
    }

    /**
     * Vykreslí jednu hranu polygonu použitím vlastností z mateřského tvaru.
     */
    private void drawEdge(Point a, Point b, Polygon shape) {
        Line edge = new Line(a, b, shape.getColor(), shape.getLineType());
        edge.setThickness(shape.getThickness());
        lineRasterizer.rasterize(edge);
    }
}