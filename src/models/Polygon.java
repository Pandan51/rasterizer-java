package models;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;

    public Polygon()
    {
        points = new ArrayList<Point>();
    }

    public void addPoint(Point point)
    {
        points.add(point);
    }

    public void clearPoints()
    {
        points.clear();
    }

    public ArrayList<Point> getPoints(){
        return points;
    }
}
