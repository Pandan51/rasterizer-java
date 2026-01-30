import math.AngleCalculator;
import models.Line;
import models.LineCanvas;
import models.LineDotted;
import rasterizers.Rasterizer;
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
import java.util.ArrayList;
//import java.util.Scanner;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

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
            }

            @Override
            public void mouseReleased(MouseEvent e) {

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

//                throw new RuntimeException("This is the angle: "+ angle +
//                        " This is the A: "+ a.getX() +", " + a.getY() +
//                        " This is the B: "+ b.getX() +", " + b.getY());

//                CreateLine();
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
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
                    panel.repaint();
                }
                else if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    isSnapMode = true;
//                    throw new RuntimeException("Shift is held");
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
            }

        });

        // CRITICAL: Panels aren't focusable by default.
        // Without these lines, KeyListener will never fire.
        panel.setFocusable(true);
        panel.requestFocusInWindow();
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


    }


}
