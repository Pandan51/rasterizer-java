package models;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;
    //
    private int lineType;

    public Polygon(boolean mode)
    {
        points = new ArrayList<Point>();
        if(mode)
        {
            lineType = 1;
        }
        else
        {
            lineType = 0;
        }
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

    public int getLineType(){ return lineType;}
}
