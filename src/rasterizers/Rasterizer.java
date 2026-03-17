package rasterizers;

import models.Line;
import models.Shapes.Ellipse;
//import models.Shapes.Rectangle;
import models.Shapes.Ellipse;

import java.awt.*;

public interface Rasterizer {

    void setColor(Color color);

    void rasterize(Line line);

    void rasterize(Ellipse ellipse);

//    void rasterize(Rectangle rectangle);
}
