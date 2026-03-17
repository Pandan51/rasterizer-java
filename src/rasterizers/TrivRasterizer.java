package rasterizers;

import models.Line;
import models.LineDotted;
import models.Point;
import models.Polygon;
import models.Shapes.Ellipse;
import rasters.Raster;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrivRasterizer implements Rasterizer {
    private final Raster raster;
    private int color;

    public TrivRasterizer(Raster raster, Color color) {
        this.raster = raster;
        this.color = color.getRGB();
    }

    @Override
    public void setColor(Color color) {
        this.color = color.getRGB();
    }

    @Override
    public void rasterize(Line line) {
        int x1 = line.getA().getX();
        int y1 = line.getA().getY();
        int x2 = line.getB().getX();
        int y2 = line.getB().getY();

        int incrementValue = (line instanceof LineDotted) ? ((LineDotted) line).getGap() : 1;
        drawGenericLine(x1, y1, x2, y2, incrementValue);
    }

    @Override
    public void rasterize(Ellipse ellipse) {
        int x0 = ellipse.getCenter().getX();
        int y0 = ellipse.getCenter().getY();
        int a = ellipse.getRx();
        int b = ellipse.getRy();
        if (a <= 0 || b <= 0) return;

        // Pro elipsu používáme přesný Bresenhamův algoritmus
        drawBresenhamEllipse(x0, y0, a, b);
    }

    /**
     * Scanline algoritmus pro vyplnění polygonu.
     * Tento algoritmus ignoruje, zda je čára tečkovaná, protože pracuje s vertexy.
     */
    public void fillPolygon(Polygon poly) {
        List<Point> points = poly.getPoints();
        if (points.size() < 3) return;

        // 1. Najít Y min a Y max
        int yMin = points.get(0).getY();
        int yMax = points.get(0).getY();
        for (Point p : points) {
            if (p.getY() < yMin) yMin = p.getY();
            if (p.getY() > yMax) yMax = p.getY();
        }

        // 2. Pro každý řádek (scanline) od yMin do yMax
        for (int y = yMin; y <= yMax; y++) {
            List<Integer> intersections = new ArrayList<>();

            // Najít průsečíky se všemi hranami polygonu
            for (int i = 0; i < points.size(); i++) {
                Point p1 = points.get(i);
                Point p2 = points.get((i + 1) % points.size()); // Uzavření hran

                // Kontrola, zda scanline protíná hranu (vynecháme horizontální hrany)
                if ((p1.getY() <= y && p2.getY() > y) || (p2.getY() <= y && p1.getY() > y)) {
                    // Výpočet X souřadnice průsečíku pomocí lineární interpolace
                    double x = p1.getX() + (double)(y - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
                    intersections.add((int) Math.round(x));
                }
            }

            // 3. Seřadit průsečíky podle X
            Collections.sort(intersections);

            // 4. Vyplnit úsečky mezi páry průsečíků (sudá/lichá pravidlo)
            for (int i = 0; i < intersections.size(); i += 2) {
                if (i + 1 < intersections.size()) {
                    int xStart = intersections.get(i);
                    int xEnd = intersections.get(i + 1);
                    for (int x = xStart; x <= xEnd; x++) {
                        raster.setPixel(x, y, color);
                    }
                }
            }
        }
    }

    private void drawGenericLine(int x1, int y1, int x2, int y2, int step) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (x1 > x2) { int tx=x1; x1=x2; x2=tx; int ty=y1; y1=y2; y2=ty; }
            double k = dy / dx;
            double q = y1 - k * x1;
            for (int x = x1; x <= x2; x += step) {
                raster.setPixel(x, (int)Math.round(k * x + q), color);
            }
        } else {
            if (y1 > y2) { int tx=x1; x1=x2; x2=tx; int ty=y1; y1=y2; y2=ty; }
            if (y2 != y1) {
                double kInv = dx / dy;
                double qInv = x1 - kInv * y1;
                for (int y = y1; y <= y2; y += step) {
                    raster.setPixel((int)Math.round(kInv * y + qInv), y, color);
                }
            } else {
                raster.setPixel(x1, y1, color);
            }
        }
    }

    private void drawBresenhamEllipse(int x0, int y0, int a, int b) {
        long a2 = (long) a * a; long b2 = (long) b * b;
        long twoA2 = 2 * a2; long twoB2 = 2 * b2;
        int x = 0; int y = b;
        long px = 0; long py = twoA2 * y;
        long p = Math.round(b2 - (a2 * b) + (0.25 * a2));
        while (px <= py) {
            drawEllipsePoints(x0, y0, x, y);
            x++; px += twoB2;
            if (p < 0) p += b2 + px;
            else { y--; py -= twoA2; p += b2 + px - py; }
        }
        p = Math.round(b2 * (x + 0.5) * (x + 0.5) + a2 * (y - 1) * (y - 1) - a2 * b2);
        while (y >= 0) {
            drawEllipsePoints(x0, y0, x, y);
            y--; py -= twoA2;
            if (p > 0) p += a2 - py;
            else { x++; px += twoB2; p += a2 - py + px; }
        }
    }

    private void drawEllipsePoints(int x0, int y0, int x, int y) {
        raster.setPixel(x0 + x, y0 + y, color);
        raster.setPixel(x0 - x, y0 + y, color);
        raster.setPixel(x0 + x, y0 - y, color);
        raster.setPixel(x0 - x, y0 - y, color);
    }
}