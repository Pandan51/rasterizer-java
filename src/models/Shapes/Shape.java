package models.Shapes;
import models.Point;

abstract class Shape {
    private Point pointA;
    private Point pointB;

    public Point getPointA() {
        return pointA;
    }

    public Point getPointB() {
        return pointB;
    }

    public abstract Point[] getPoints();

}
