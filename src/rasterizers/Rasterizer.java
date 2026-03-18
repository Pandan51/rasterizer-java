package rasterizers;

import models.Line;
import models.Shapes.Ellipse;
import java.awt.*;

public interface Rasterizer {
    void setColor(Color color);

    // Nový setter pro tloušťku v rasterizéru
    void setThickness(int thickness);

    void rasterize(Line line);

    void rasterize(Ellipse ellipse);
}