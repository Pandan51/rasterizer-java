import math.AngleCalculator;
import models.*;
import models.Point;
import models.Polygon;
import models.Shapes.Ellipse;
import models.Shapes.Rectangle;
import rasterizers.TrivRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;
import rasterizers.CanvasRasterizer;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.Serial;
import java.util.ArrayList;

/**
 * Hlavní třída aplikace sjednocující všechny funkce.
 */
public class App {

    private final JPanel panel;
    private final LineCanvas lineCanvas;
    private final Raster raster;
    private final CanvasRasterizer canvasRasterizer;
    private final TrivRasterizer lineRasterizer;

    private Point a, b;
    private boolean isDottedMode = false;
    private boolean isSnapMode = false;
    private boolean isFilledMode = false; // Příznak, zda mají být nové tvary vyplněné

    private enum Tool { LINE, RECTANGLE, ELLIPSE, POLYGON }
    private Tool currentTool = Tool.LINE;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public App(int width, int height) {
        JFrame frame = new JFrame("PGRF1 Paint - Unified System");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new TrivRasterizer(raster, Color.RED);
        canvasRasterizer = new CanvasRasterizer(lineRasterizer);
        lineCanvas = new LineCanvas();

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                raster.repaint(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        createCallbacks();
    }

    private void start() {
        redraw();
    }

    private void createCallbacks() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point clickPoint = new Point(e.getX(), e.getY());
                if (currentTool == Tool.POLYGON) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        handlePolygonPoint(clickPoint);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        finishCurrentPolygon();
                    }
                } else {
                    a = clickPoint;
                }
                panel.requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool == Tool.POLYGON) return;
                b = getFinalPoint(e.getX(), e.getY());
                lineCanvas.addShape(createShape(a, b));
                redraw();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool == Tool.POLYGON) return;
                b = getFinalPoint(e.getX(), e.getY());
                redraw();
                renderPreview(createShape(a, b));
            }
        };

        panel.addMouseListener(ma);
        panel.addMouseMotionListener(ma);

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_CONTROL -> isDottedMode = true;
                    case KeyEvent.VK_SHIFT -> isSnapMode = true;
                    case KeyEvent.VK_F -> {
                        isFilledMode = !isFilledMode; // Přepnutí režimu výplně
                        System.out.println("Fill Mode: " + (isFilledMode ? "ON" : "OFF"));
                    }
                    case KeyEvent.VK_C -> {
                        lineCanvas.clear();
                        redraw();
                    }
                    case KeyEvent.VK_ENTER -> finishCurrentPolygon();
                    case KeyEvent.VK_1 -> currentTool = Tool.LINE;
                    case KeyEvent.VK_2 -> currentTool = Tool.RECTANGLE;
                    case KeyEvent.VK_3 -> currentTool = Tool.ELLIPSE;
                    case KeyEvent.VK_4 -> currentTool = Tool.POLYGON;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) isDottedMode = false;
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) isSnapMode = false;
            }
        });
        panel.setFocusable(true);
        panel.requestFocusInWindow();
    }

    private Point getFinalPoint(int x, int y) {
        Point p = new Point(x, y);
        return isSnapMode ? AngleCalculator.getSnappedPoint(a, p) : p;
    }

    private Polygon createShape(Point p1, Point p2) {
        Polygon shape = switch (currentTool) {
            case LINE -> new Line(p1, p2, Color.RED, isDottedMode);
            case RECTANGLE -> new Rectangle(p1, p2, Color.RED, isDottedMode);
            case ELLIPSE -> {
                int centerX = (p1.getX() + p2.getX()) / 2;
                int centerY = (p1.getY() + p2.getY()) / 2;
                Point center = new Point(centerX, centerY);
                int rx = Math.abs(p2.getX() - p1.getX()) / 2;
                int ry = Math.abs(p2.getY() - p1.getY()) / 2;
                yield new Ellipse(center, rx, ry, Color.RED, isDottedMode);
            }
            default -> new Line(p1, p2, Color.RED, isDottedMode);
        };

        shape.setFilled(isFilledMode);
        return shape;
    }

    private void handlePolygonPoint(Point p) {
        ArrayList<models.Polygon> shapes = lineCanvas.getShapes();
        models.Polygon poly;

        if (shapes.isEmpty() || shapes.get(shapes.size() - 1).isClosed()) {
            poly = new models.Polygon(Color.RED, isDottedMode);
            poly.setClosed(false);
            poly.setFilled(isFilledMode);
            lineCanvas.addShape(poly);
        } else {
            poly = shapes.get(shapes.size() - 1);
        }

        // Snapping k předchozímu bodu
        if (isSnapMode && !poly.getPoints().isEmpty()) {
            p = AngleCalculator.getSnappedPoint(poly.getPoints().get(poly.getPoints().size() - 1), p);
        }

        poly.addPoint(p);
        redraw();
    }

    private void finishCurrentPolygon() {
        ArrayList<models.Polygon> shapes = lineCanvas.getShapes();
        if (!shapes.isEmpty()) {
            models.Polygon lastShape = shapes.get(shapes.size() - 1);
            if (!lastShape.isClosed()) {
                lastShape.setClosed(true);
                redraw();
            }
        }
    }

    private void redraw() {
        raster.setClearColor(0xaaaaaa);
        raster.clear();
        canvasRasterizer.rasterize(lineCanvas);
        panel.repaint();
    }

    private void renderPreview(models.Polygon preview) {
        // Pokud je zapnutá výplň, musíme ji nejdříve vykreslit v náhledu
        if (preview.isFilled()) {
            lineRasterizer.setColor(preview.getColor());
            lineRasterizer.fillPolygon(preview);
        }

        if (preview instanceof Ellipse) {
            lineRasterizer.setColor(preview.getColor());
            lineRasterizer.rasterize((Ellipse) preview);
        } else {
            LineCanvas temp = new LineCanvas();
            temp.addShape(preview);
            canvasRasterizer.rasterize(temp);
        }
        panel.repaint();
    }

    private void findClosestPoint(int x, int y) {
        double minDistance = 15; // Tolerance v pixelech pro "chycení" bodu
        selectedPoint = null;
        selectedPolygon = null;

        ArrayList<models.Polygon> shapes = lineCanvas.getShapes();

        // Procházíme od konce, abychom nejdříve kontrolovali nejnovější (horní) tvary
        for (int i = shapes.size() - 1; i >= 0; i--) {
            models.Polygon shape = shapes.get(i);

            for (models.Point p : shape.getPoints()) {
                double dist = Math.sqrt(Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2));

                if (dist < minDistance) {
                    minDistance = dist;
                    selectedPoint = p;
                    selectedPolygon = shape;

                    // Jakmile najdeme nejbližší bod v nejvrchnějším tvaru, končíme hledání
                    return;
                }
            }
        }
    }
}