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
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.Serial;
import java.util.ArrayList;

public class App {
    private final JPanel panel;
    private final LineCanvas lineCanvas;
    private final Raster raster;
    private final CanvasRasterizer canvasRasterizer;
    private final TrivRasterizer lineRasterizer;

    private Point a, b;
    private Polygon.LineType currentType = Polygon.LineType.SOLID;
    private boolean isSnapMode = false;
    private boolean isFilledMode = false;
    private Color currentColor = Color.RED;
    private Color currentFillColor = Color.YELLOW;
    private int currentThickness = 1;

    private enum Tool { LINE, RECTANGLE, SQUARE, ELLIPSE, CIRCLE, POLYGON, EDIT, MOVE, ERASE, DELETE, BUCKET }
    private Tool currentTool = Tool.LINE;

    private Point selectedPoint = null;
    private Polygon selectedPolygon = null;
    private Point lastMousePos = null;

    // UI Components for synchronization
    private JButton colorBtn;
    private JButton fillColorBtn;
    private JSlider thickSlider;
    private JComboBox<String> styleCombo;
    private JCheckBox fillCheck;

    /**
     * Vstupní bod programu. Vytvoří a spustí aplikaci na hlavním vlákně Swing.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(1100, 750).start());
    }

    /**
     * Konstruktor aplikace. Inicializuje okno, rastry, plátno a komponenty uživatelského rozhraní.
     */
    public App(int width, int height) {
        JFrame frame = new JFrame("Malovani");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height - 150);
        lineRasterizer = new TrivRasterizer(raster, currentColor);
        canvasRasterizer = new CanvasRasterizer(lineRasterizer);
        lineCanvas = new LineCanvas();

        panel = new JPanel() {
            @Serial private static final long serialVersionUID = 1L;
            @Override public void paintComponent(Graphics g) { super.paintComponent(g); raster.repaint(g); }
        };
        panel.setPreferredSize(new Dimension(width, height - 150));

        frame.add(createControlPanel(), BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        createCallbacks();
    }

    /**
     * Sestaví a vrátí horní ovládací panel s tlačítky nástrojů a nastavením vlastností.
     */
    private JPanel createControlPanel() {
        JPanel mainCtrl = new JPanel(new BorderLayout());
        mainCtrl.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel toolPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        String[] toolNames = {"Čára", "Obd.", "Čtverec", "Elipsa", "Kruh", "Polygon", "Edit bodu", "Posun", "Smazani bodu", "Smazat obj.", "Kyblík"};
        Tool[] tools = Tool.values();

        for (int i = 0; i < toolNames.length; i++) {
            final Tool t = (i < tools.length) ? tools[i] : Tool.LINE;
            JButton btn = new JButton(toolNames[i]);
            btn.addActionListener(e -> {
                currentTool = t;
                panel.requestFocusInWindow();
            });
            toolPanel.add(btn);
        }

        JPanel settingsPanel = new JPanel();

        colorBtn = new JButton("Barva Čáry");
        colorBtn.setBackground(currentColor);
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(null, "Barva Čáry", currentColor);
            if (c != null) {
                currentColor = c;
                colorBtn.setBackground(c);
                if (selectedPolygon != null) {
                    selectedPolygon.setColor(currentColor);
                    redraw();
                }
            }
            panel.requestFocusInWindow();
        });

        fillColorBtn = new JButton("Barva Výplně");
        fillColorBtn.setBackground(currentFillColor);
        fillColorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(null, "Barva Výplně", currentFillColor);
            if (c != null) {
                currentFillColor = c;
                fillColorBtn.setBackground(c);
                if (selectedPolygon != null) {
                    selectedPolygon.setFillColor(currentFillColor);
                    redraw();
                }
            }
            panel.requestFocusInWindow();
        });

        thickSlider = new JSlider(1, 20, 1);
        thickSlider.addChangeListener(e -> {
            currentThickness = thickSlider.getValue();
            if (selectedPolygon != null) {
                selectedPolygon.setThickness(currentThickness);
                redraw();
            }
            panel.requestFocusInWindow();
        });

        String[] styles = {"Plná", "Tečkovaná", "Čárkovaná"};
        styleCombo = new JComboBox<>(styles);
        styleCombo.addActionListener(e -> {
            currentType = Polygon.LineType.values()[styleCombo.getSelectedIndex()];
            if (selectedPolygon != null) {
                selectedPolygon.setLineType(currentType);
                redraw();
            }
            panel.requestFocusInWindow();
        });

        fillCheck = new JCheckBox("Auto-výplň");
        fillCheck.addActionListener(e -> {
            isFilledMode = fillCheck.isSelected();
            if (selectedPolygon != null) {
                selectedPolygon.setFilled(isFilledMode);
                redraw();
            }
            panel.requestFocusInWindow();
        });

        JButton clearBtn = new JButton("Vymazat plátno");
        clearBtn.addActionListener(e -> { lineCanvas.clear(); redraw(); panel.requestFocusInWindow(); });

        settingsPanel.add(colorBtn);
        settingsPanel.add(fillColorBtn);
        settingsPanel.add(new JLabel(" Tloušťka: "));
        settingsPanel.add(thickSlider);
        settingsPanel.add(styleCombo);
        settingsPanel.add(fillCheck);
        settingsPanel.add(clearBtn);

        mainCtrl.add(toolPanel, BorderLayout.WEST);
        mainCtrl.add(settingsPanel, BorderLayout.CENTER);
        return mainCtrl;
    }

    /**
     * Vytvoří a připojí posluchače událostí pro myš a klávesnici (kreslení, úpravy tvarů).
     */
    private void createCallbacks() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = new Point(e.getX(), e.getY());

                if (currentTool == Tool.BUCKET) {
                    lineRasterizer.seedFill(e.getX(), e.getY(), currentColor.getRGB());
                    panel.repaint();
                    return;
                }

                if (isTransformationTool()) {
                    findSelection(e.getX(), e.getY());
                    syncUIWithSelected();
                }

                if (selectedPolygon != null) {
                    if (currentTool == Tool.DELETE) {
                        lineCanvas.getShapes().remove(selectedPolygon);
                        selectedPolygon = null;
                        redraw();
                    } else if (currentTool == Tool.ERASE && selectedPoint != null) {
                        selectedPolygon.getPoints().remove(selectedPoint);
                        if (selectedPolygon instanceof Ellipse) ((Ellipse) selectedPolygon).setPerfect(false);
                        if (selectedPolygon.getPoints().size() < 2) lineCanvas.getShapes().remove(selectedPolygon);
                        selectedPolygon = null;
                        redraw();
                    }
                }

                if (isDrawingTool()) {
                    a = new Point(e.getX(), e.getY());
                    if (currentTool == Tool.POLYGON) {
                        if (SwingUtilities.isLeftMouseButton(e)) handlePolygonPoint(a);
                        else finishCurrentPolygon();
                    }
                }
                panel.requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDrawingTool() || currentTool == Tool.POLYGON) {
                    // Stay selected
                } else {
                    b = getFinalPoint(e.getX(), e.getY(), currentTool);
                    lineCanvas.addShape(createShape(a, b, currentTool));
                    redraw();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPolygon != null && (currentTool == Tool.EDIT || currentTool == Tool.MOVE)) {
                    int dx = e.getX() - lastMousePos.getX();
                    int dy = e.getY() - lastMousePos.getY();
                    if (currentTool == Tool.EDIT && selectedPoint != null) {
                        selectedPoint.setX(e.getX()); selectedPoint.setY(e.getY());
                        if (selectedPolygon instanceof Ellipse) ((Ellipse) selectedPolygon).setPerfect(false);
                    } else if (currentTool == Tool.MOVE) {
                        selectedPolygon.move(dx, dy);
                    }
                    lastMousePos = new Point(e.getX(), e.getY());
                    redraw();
                } else if (isDrawingTool() && currentTool != Tool.POLYGON) {
                    b = getFinalPoint(e.getX(), e.getY(), currentTool);
                    redraw();
                    renderPreview(createShape(a, b, currentTool));
                }
            }
        };
        panel.addMouseListener(ma);
        panel.addMouseMotionListener(ma);

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT -> isSnapMode = true;
                    case KeyEvent.VK_ENTER -> finishCurrentPolygon();
                    case KeyEvent.VK_C -> { lineCanvas.clear(); redraw(); }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) isSnapMode = false;
            }
        });
        panel.setFocusable(true);
        panel.requestFocusInWindow();
    }

    /**
     * Synchronizuje hodnoty UI prvků (barvy, posuvníky) s právě vybraným objektem.
     */
    private void syncUIWithSelected() {
        if (selectedPolygon != null) {
            currentColor = selectedPolygon.getColor();
            currentFillColor = selectedPolygon.getFillColor();
            currentThickness = selectedPolygon.getThickness();
            currentType = selectedPolygon.getLineType();
            isFilledMode = selectedPolygon.isFilled();

            // Update UI components
            colorBtn.setBackground(currentColor);
            fillColorBtn.setBackground(currentFillColor);
            thickSlider.setValue(currentThickness);
            styleCombo.setSelectedIndex(currentType.ordinal());
            fillCheck.setSelected(isFilledMode);
        }
    }

    /**
     * Zjistí, zda se na daných souřadnicích nachází nějaký tvar nebo jeho editační bod.
     */
    private void findSelection(int x, int y) {
        selectedPoint = null; selectedPolygon = null;
        ArrayList<Polygon> shapes = lineCanvas.getShapes();
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Polygon s = shapes.get(i);
            for (Point p : s.getPoints()) {
                if (Math.hypot(p.getX() - x, p.getY() - y) < 15) {
                    selectedPoint = p; selectedPolygon = s; return;
                }
            }
            if (s.contains(x, y)) { selectedPolygon = s; return; }
        }
    }

    /**
     * Tovární metoda pro generování konkrétního typu tvaru na základě zvoleného nástroje.
     */
    private Polygon createShape(Point p1, Point p2, Tool tool) {
        Polygon shape = switch (tool) {
            case SQUARE, RECTANGLE -> new Rectangle(p1, p2, currentColor, currentType);
            case CIRCLE, ELLIPSE -> {
                int centerX = (p1.getX() + p2.getX()) / 2;
                int centerY = (p1.getY() + p2.getY()) / 2;
                int rx = Math.abs(p2.getX() - p1.getX()) / 2;
                int ry = Math.abs(p2.getY() - p1.getY()) / 2;
                yield new Ellipse(new Point(centerX, centerY), rx, ry, currentColor, currentType);
            }
            default -> new Line(p1, p2, currentColor, currentType);
        };
        shape.setFillColor(currentFillColor);
        shape.setThickness(currentThickness);
        shape.setFilled(isFilledMode);
        return shape;
    }

    /**
     * Vypočítá konečnou pozici druhého bodu s ohledem na snapping (Shift) nebo vynucený poměr stran (Čtverec/Kruh).
     */
    private Point getFinalPoint(int x, int y, Tool tool) {
        Point p = new Point(x, y);
        if (isSnapMode) p = AngleCalculator.getSnappedPoint(a, p);
        if (tool == Tool.SQUARE || tool == Tool.CIRCLE) {
            int dx = p.getX() - a.getX();
            int dy = p.getY() - a.getY();
            int size = Math.max(Math.abs(dx), Math.abs(dy));
            p = new Point(a.getX() + (dx > 0 ? size : -size), a.getY() + (dy > 0 ? size : -size));
        }
        return p;
    }

    /**
     * Zpracovává postupně přidávané body pro nástroj Polygon.
     */
    private void handlePolygonPoint(Point p) {
        ArrayList<Polygon> shapes = lineCanvas.getShapes();
        Polygon poly;
        if (shapes.isEmpty() || shapes.get(shapes.size() - 1).isClosed()) {
            poly = new Polygon(currentColor, currentFillColor, currentType);
            poly.setClosed(false); poly.setFilled(isFilledMode);
            poly.setThickness(currentThickness);
            lineCanvas.addShape(poly);
        } else { poly = shapes.get(shapes.size() - 1); }

        poly.addPoint(p);
        selectedPolygon = poly;
        syncUIWithSelected();
        redraw();
    }

    /**
     * Uzavře aktuálně kreslený polygon.
     */
    private void finishCurrentPolygon() {
        ArrayList<Polygon> shapes = lineCanvas.getShapes();
        if (!shapes.isEmpty()) {
            Polygon lastShape = shapes.get(shapes.size() - 1);
            if (!lastShape.isClosed()) { lastShape.setClosed(true); redraw(); }
        }
    }

    /**
     * Smaže celý rastr a provede kompletní překreslení všech objektů z plátna.
     */
    private void redraw() {
        raster.clear();
        canvasRasterizer.rasterize(lineCanvas, selectedPolygon);
        panel.repaint();
    }

    /**
     * Zajišťuje vykreslování dočasného tvaru ("ducha") během tažení myší.
     */
    private void renderPreview(Polygon preview) {
        if (preview.isFilled()) {
            lineRasterizer.setColor(preview.getFillColor());
            lineRasterizer.fillPolygon(preview);
        }
        lineRasterizer.setColor(preview.getColor());
        lineRasterizer.setThickness(preview.getThickness());
        if (preview instanceof Ellipse && ((Ellipse) preview).isPerfect()) {
            lineRasterizer.rasterize((Ellipse) preview);
        } else {
            LineCanvas temp = new LineCanvas();
            temp.addShape(preview);
            canvasRasterizer.rasterize(temp, preview);
        }
        panel.repaint();
    }

    /**
     * Zjistí, zda aktuálně zvolený nástroj slouží ke kreslení nových tvarů.
     */
    private boolean isDrawingTool() { return currentTool.ordinal() <= Tool.POLYGON.ordinal(); }

    /**
     * Zjistí, zda aktuálně zvolený nástroj slouží k úpravě již existujících tvarů.
     */
    private boolean isTransformationTool() { return !isDrawingTool() && currentTool != Tool.BUCKET; }

    /**
     * Úvodní překreslení plochy po startu.
     */
    public void start() { redraw(); }
}