package rasterizers;


import models.Line;
import models.LineCanvas;

public class CanvasRasterizer{

    private Rasterizer lineRasterizer;

    public CanvasRasterizer(Rasterizer lineRasterizer){
        this.lineRasterizer = lineRasterizer;
    }
    public void rasterize(LineCanvas lineCanvas){
        for(Line line : lineCanvas.getLines()){
            lineRasterizer.rasterize(line);
        }
    }
}
