import math.AngleCalculator;
import models.Line;
import models.LineCanvas;
import models.LineDotted;
import models.Polygon;
import rasterizers.TrivRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;
import rasterizers.CanvasRasterizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
//import java.util.Scanner;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;



import models.Point;

public class App {

    private final JPanel panel;
    private final LineCanvas lineCanvas;



    private final Raster raster;
    private final CanvasRasterizer canvasRasterizer;

//    private final AngleCalculator angleCalculator;

    MouseAdapter mouseAdapter;
    KeyListener keyListener;

    Point a, b;
    boolean isDottedMode = false;
    boolean isSnapMode = false;
    boolean isPolygonMode = false;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }
    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    // Pohyby a kliknutí myši
    void createMouseCallbacks(){
        mouseAdapter = new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                a = new Point(e.getX(), e.getY());

                if(isPolygonMode)
                {
                    if(lineCanvas.getPolygons().isEmpty())
                    {
                        lineCanvas.addPolygon(new Polygon(isDottedMode));
                    }
                    Polygon currentPolygon = lineCanvas.getPolygons().getLast();
                    Point temp;
                    if(isSnapMode && !currentPolygon.getPoints().isEmpty())
                    {
                        a = AngleCalculator.getSnappedPoint(currentPolygon.getPoints().getLast(), a);
                    }
                    currentPolygon.addPoint(a);
                    clear(0xaaaaaa);
                    panel.repaint();
                    canvasRasterizer.rasterize(lineCanvas);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(isPolygonMode)
                {
                    return;
                }
                b = new Point(e.getX(), e.getY());
                Line line;
                if(isSnapMode)
                {
                    double angle = AngleCalculator.getAngle(a,b);
                    b = AngleCalculator.getSnappedB(a,b, angle);
                }
                if(isDottedMode)
                {
                    line = new LineDotted(a,b, Color.red, 5);
                }
                else {
                    line = new Line(a, b, Color.red);
                }
                lineCanvas.addLine(line);
                clear(0xaaaaaa);
                panel.repaint();
                canvasRasterizer.rasterize(lineCanvas);

                double angle = AngleCalculator.getAngle(a,b);

//                throw new RuntimeException("This is the angle: "+ angle);
//                CreateLine();
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                if(isPolygonMode)
                {
                    return;
                }
                clear(0xaaaaaa);
                canvasRasterizer.rasterize(lineCanvas);
                b = new Point(e.getX(), e.getY());
                if(isSnapMode)
                {
                    double angle = AngleCalculator.getAngle(a,b);
                    b = AngleCalculator.getSnappedB(a,b, angle);
                }
                CreateLine();
//                panel.repaint();
            }


        };

        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);

    }

    // Když zmáčknuta klávesa
    void createKeyCallbacks() {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Check for the specific key, e.g., the 'D' key or Shift
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isDottedMode = true;
                }
                else if(e.getKeyCode() == KeyEvent.VK_C) {
                    clear(0xaaaaaa);
                    lineCanvas.clearLines();
                    lineCanvas.clearPolygons();
                    panel.repaint();
                }
                else if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    isSnapMode = true;
//                    throw new RuntimeException("Shift is held");
                }
                else if(e.getKeyCode() == KeyEvent.VK_X){
                    if(!isPolygonMode)
                    {
                        lineCanvas.addPolygon(new Polygon(isDottedMode));
                    }
                    isPolygonMode = true;

//                    throw new RuntimeException("X is held");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isDottedMode = false;
                }
                else if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    isSnapMode = false;
//                    throw new RuntimeException("Shift is released");
                }
                else if(e.getKeyCode() == KeyEvent.VK_X){
                    isPolygonMode = false;
//                    throw new RuntimeException("X is released");
                }
            }

        });

        // CRITICAL: Panels aren't focusable by default.
        // Without these lines, KeyListener will never fire.
        panel.setFocusable(true);
        panel.requestFocusInWindow();
    }

private void createKeyBindings() {
    InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = panel.getActionMap();

    // Define the Keys
    setupBinding(inputMap, actionMap, "X", KeyEvent.VK_X);
    setupBinding(inputMap, actionMap, "SHIFT", KeyEvent.VK_SHIFT);
    setupBinding(inputMap, actionMap, "CONTROL", KeyEvent.VK_CONTROL);

    // Special case for 'C' (Instant Trigger)
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "clearCanvas");
    actionMap.put("clearCanvas", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            clear(0xaaaaaa);
            lineCanvas.clearLines();
            lineCanvas.clearPolygons();
            panel.repaint();
        }
    });
}

    private void setupBinding(InputMap im, ActionMap am, String name, int keyCode) {
        // 1. Action when Pressed
        im.put(KeyStroke.getKeyStroke(keyCode, 0, false), name + "Pressed");
        am.put(name + "Pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateKeyState(keyCode, true);
            }
        });

        // 2. Action when Released
        im.put(KeyStroke.getKeyStroke(keyCode, 0, true), name + "Released");
        am.put(name + "Released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateKeyState(keyCode, false);
            }
        });
    }

    private void updateKeyState(int keyCode, boolean isPressed) {
        switch (keyCode) {
            case KeyEvent.VK_X -> {
//                polygonMode = isPressed;
                if(!isPolygonMode && isPressed)
                {
                    isPolygonMode = true;
                    lineCanvas.addPolygon(new Polygon(isDottedMode));
//                    throw new RuntimeException("New polygon");
                }
                else if (!isPressed)
                {
                    isPolygonMode = false;
                }
            }
            case KeyEvent.VK_SHIFT -> {
//                isSnapMode = isPressed;
                if(isPressed)
                {

                    isSnapMode = true;
                    System.out.println(isSnapMode);
                }
                else if (!isPressed)
                {
                    isSnapMode = false;
                    System.out.println(isSnapMode);
                }
//                throw new RuntimeException("Shift action");
            }
            case KeyEvent.VK_CONTROL -> {
                if(!isDottedMode && isPressed)
                {
                    isDottedMode = true;
                    System.out.println(isDottedMode);
                }
                else if (!isPressed)
                {
                    isDottedMode = false;
                    System.out.println(isDottedMode);
                }
//                isDottedMode = isPressed;
//                throw new RuntimeException("Control action" + isPressed);
            }
        }
    }

    void CreateLine()
    {
        TrivRasterizer rasterizer = new TrivRasterizer(raster, Color.red);
        Line line;
        if(isSnapMode)
        {
            double angle = AngleCalculator.getAngle(a,b);
            b = AngleCalculator.getSnappedB(a,b, angle);
        }
        if(isDottedMode)
        {
            line = new LineDotted(a,b, Color.red, 5);
        }
        else {
            line = new Line(a, b, Color.red);
        }
        rasterizer.rasterize(line);
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("Delta : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        TrivRasterizer rasterizer = new TrivRasterizer(raster, Color.red);
        canvasRasterizer = new CanvasRasterizer(rasterizer);
        lineCanvas = new LineCanvas();
//        angleCalculator = new AngleCalculator();

        // TODO: Pozdeji odstranit
        // Test pro polygon
        Polygon testPolygon = new Polygon(true);
        testPolygon.addPoint(new Point(200,300));
        testPolygon.addPoint(new Point(150,200));
        testPolygon.addPoint(new Point(300,400));
        testPolygon.addPoint(new Point(500,500));
        testPolygon.addPoint(new Point(50,50));
        lineCanvas.addPolygon(testPolygon);


        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));


        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        //
        createMouseCallbacks();

        createKeyCallbacks();
        // Complicated
//        createKeyBindings();


    }


}
