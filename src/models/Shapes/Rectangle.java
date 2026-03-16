package models.Shapes;

import models.Point;

import java.util.ArrayList;

public class Rectangle extends Shape {
    private Point pointA;
    private Point pointB;

    public Point getPointA() {
        return pointA;
    }

    public Point getPointB() {
        return pointB;
    }

    public Rectangle(Point A, Point B){
        pointA = A;
        pointB = B;
    }

    @Override
    public ArrayList<Point> getPoints() {
        int x_diff = pointA.getX() - pointB.getX();
        int y_diff = pointA.getY() - pointB.getY();

        Point x = new Point(x_diff, pointA.getY());
        Point y = new Point(pointA.getX(), y_diff);
        Point[] arr = {pointA, x, y, pointB};
        return arr;

    }


}
