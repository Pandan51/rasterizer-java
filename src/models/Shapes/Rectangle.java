package models.Shapes;

import models.Point;
import models.Polygon;
import java.awt.*;

public class Rectangle extends Polygon {
    public Rectangle(Point a, Point b, Color color, boolean isDotted) {
        super(color, isDotted);

        // Vytvoření 4 bodů obdélníku
        this.addPoint(a); // Horní levý (např.)
        this.addPoint(new Point(b.getX(), a.getY())); // Horní pravý
        this.addPoint(b); // Dolní pravý
        this.addPoint(new Point(a.getX(), b.getY())); // Dolní levý

        this.setClosed(true); // Obdélník je uzavřený
    }
}