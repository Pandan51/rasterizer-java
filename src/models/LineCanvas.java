package models;

import java.util.ArrayList;
import java.util.List;
import models.Line;

public class LineCanvas {
    private final ArrayList<Line> lines;
    private final ArrayList<Polygon> polygons;

    public LineCanvas()
    {
        lines = new ArrayList<Line>();
        polygons = new ArrayList<Polygon>();
    }

    public void addLine(Line line)
    {
        lines.add(line);
    }

    public void clearLines()
    {
        lines.clear();
    }

    public ArrayList<Line> getLines(){
        return lines;
    }

    public void addPolygon(Polygon polygon)
    {
        polygons.add(polygon);
    }

    public void clearPolygons()
    {
        polygons.clear();
    }

    public ArrayList<Polygon> getPolygons(){
        return polygons;
    }

}
