package rasterizers;


import models.Line;
import models.LineCanvas;
import models.Polygon;
import models.Point;

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

                for (int pointA = 0; pointA < points.size()-1; pointA++) {
                    int pointB = pointA + 1;
                    lineRasterizer.rasterize(new Line(points.get(pointA), points.get(pointB), Color.RED));
                }

                lineRasterizer.rasterize(new Line(points.getFirst(), points.getLast(), Color.RED));
            }
        }




    }
}
