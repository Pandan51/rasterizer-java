package rasterizers;

import models.*;
import models.Point;
import models.Polygon;
import models.Shapes.Ellipse;
import rasters.Raster;
import java.awt.Color;
import java.util.*;

public class TrivRasterizer implements Rasterizer {
    private final Raster raster;
    private int color;
    private int thickness = 1;

    public TrivRasterizer(Raster raster, Color color) {
        this.raster = raster;
        this.color = color.getRGB();
    }

    @Override
    public void setColor(Color color) { this.color = color.getRGB(); }
    @Override
    public void setThickness(int thickness) { this.thickness = thickness; }

    private void setThickPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= raster.getWidth() || y >= raster.getHeight()) return;
        if (thickness <= 1) {
            raster.setPixel(x, y, color);
            return;
        }
        int offset = thickness / 2;
        for (int dx = -offset; dx < -offset + thickness; dx++) {
            for (int dy = -offset; dy < -offset + thickness; dy++) {
                int px = x + dx; int py = y + dy;
                if (px >= 0 && py >= 0 && px < raster.getWidth() && py < raster.getHeight())
                    raster.setPixel(px, py, color);
            }
        }
    }

    /**
     * Bucket Fill (Seed Fill) - Iterative approach using a Queue.
     */
    public void seedFill(int x, int y, int fillRGB) {
        if (x < 0 || y < 0 || x >= raster.getWidth() || y >= raster.getHeight()) return;
        int targetRGB = raster.getPixel(x, y);
        if (targetRGB == fillRGB) return;

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int px = p.getX(); int py = p.getY();

            if (px < 0 || py < 0 || px >= raster.getWidth() || py >= raster.getHeight()) continue;
            if (raster.getPixel(px, py) == targetRGB) {
                raster.setPixel(px, py, fillRGB);
                queue.add(new Point(px + 1, py));
                queue.add(new Point(px - 1, py));
                queue.add(new Point(px, py + 1));
                queue.add(new Point(px, py - 1));
            }
        }
    }

    @Override
    public void rasterize(Line line) {
        int step = (line.getLineType() == Polygon.LineType.DOTTED) ? 5 : 1;
        int dashLen = (line.getLineType() == Polygon.LineType.DASHED) ? 10 : 0;
        drawGenericLine(line.getA().getX(), line.getA().getY(),
                line.getB().getX(), line.getB().getY(), step, dashLen);
    }

    private void drawGenericLine(int x1, int y1, int x2, int y2, int step, int dashLen) {
        double dx = x2 - x1; double dy = y2 - y1;
        int count = 0;
        boolean draw = true;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (x1 > x2) { int tx=x1; x1=x2; x2=tx; int ty=y1; y1=y2; y2=ty; }
            double k = dy / dx; double q = y1 - k * x1;
            for (int x = x1; x <= x2; x++) {
                if (dashLen > 0) {
                    if (count % dashLen == 0) draw = !draw;
                    if (draw) setThickPixel(x, (int)Math.round(k * x + q));
                } else if (count % step == 0) {
                    setThickPixel(x, (int)Math.round(k * x + q));
                }
                count++;
            }
        } else {
            if (y1 > y2) { int tx=x1; x1=x2; x2=tx; int ty=y1; y1=y2; y2=ty; }
            if (y2 != y1) {
                double kInv = dx / dy; double qInv = x1 - kInv * y1;
                for (int y = y1; y <= y2; y++) {
                    if (dashLen > 0) {
                        if (count % dashLen == 0) draw = !draw;
                        if (draw) setThickPixel((int)Math.round(kInv * y + qInv), y);
                    } else if (count % step == 0) {
                        setThickPixel((int)Math.round(kInv * y + qInv), y);
                    }
                    count++;
                }
            } else { setThickPixel(x1, y1); }
        }
    }

    @Override
    public void rasterize(Ellipse ellipse) {
        int x0 = ellipse.getCenter().getX();
        int y0 = ellipse.getCenter().getY();
        int a = ellipse.getRx();
        int b = ellipse.getRy();
        if (a <= 0 || b <= 0) return;

        // Midpoint Ellipse Algorithm (Bresenham-like)
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
        setThickPixel(x0 + x, y0 + y);
        setThickPixel(x0 - x, y0 + y);
        setThickPixel(x0 + x, y0 - y);
        setThickPixel(x0 - x, y0 - y);
    }

    /**
     * Vector Fill (Scanline Algorithm) for Polygons.
     */
    public void fillPolygon(Polygon poly) {
        List<Point> points = poly.getPoints();
        if (points.size() < 3) return;

        int yMin = points.get(0).getY();
        int yMax = points.get(0).getY();
        for (Point p : points) {
            yMin = Math.min(yMin, p.getY());
            yMax = Math.max(yMax, p.getY());
        }

        for (int y = yMin; y <= yMax; y++) {
            List<Integer> intersections = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                Point p1 = points.get(i);
                Point p2 = points.get((i + 1) % points.size());
                if ((p1.getY() <= y && p2.getY() > y) || (p2.getY() <= y && p1.getY() > y)) {
                    double x = p1.getX() + (double)(y - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
                    intersections.add((int) Math.round(x));
                }
            }
            Collections.sort(intersections);
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
}