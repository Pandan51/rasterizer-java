package models;

import java.util.ArrayList;
import java.util.List;
import models.Line;

public class LineCanvas {
    private ArrayList<Line> lines;

    public LineCanvas()
    {
        lines = new ArrayList<Line>();
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

}
