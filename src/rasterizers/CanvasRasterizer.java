package rasterizers;


import models.*;
import models.Point;
import models.Polygon;

import java.awt.*;
import java.util.ArrayList;

public class CanvasRasterizer{

    private final Rasterizer lineRasterizer;

    public CanvasRasterizer(Rasterizer lineRasterizer){
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(LineCanvas lineCanvas){
        for(Line line : lineCanvas.getLines()){
            lineRasterizer.rasterize(line);
        }

        // Rasterize polygons in LineCanvas
        for(Polygon polygon : lineCanvas.getPolygons()){
            ArrayList<Point> points = polygon.getPoints();
            if(points.size() > 2) {
                Line tempLine;
                for (int pointA = 0; pointA < points.size()-1; pointA++) {
                    int pointB = pointA + 1;
                    if(polygon.getLineType() == 1)
                    {
                        tempLine = new LineDotted(points.get(pointA), points.get(pointB), Color.RED, 5);
                    }
                    else {
                        tempLine = new Line(points.get(pointA), points.get(pointB), Color.RED);
                    }

                    lineRasterizer.rasterize(tempLine);
                }

                if(polygon.getLineType() == 1)
                {
                    tempLine = new LineDotted(points.getFirst(), points.getLast(), Color.RED, 5);
                }
                else {
                    tempLine = new Line(points.getFirst(), points.getLast(), Color.RED);
                }
                lineRasterizer.rasterize(tempLine);
            }
        }




    }
}
