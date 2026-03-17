package rasterizers;
import models.Line;
import models.LineDotted;
import models.Shapes.Ellipse;
import models.Shapes.Ellipse;
import rasters.Raster;

import java.awt.*;

public class TrivRasterizer implements Rasterizer {

    Raster raster;
    int color;

    public TrivRasterizer(Raster raster, Color color) {
        this.raster = raster;
        this.color = color.getRGB();
    }

    @Override
    public void setColor(Color color) {

    }

    @Override
    public void rasterize(Line line) {
        // TODO vyřešit hranice okna
        // TODO vyřešit svislou úsečku

        int x1 = line.getA().getX();
        int y1 = line.getA().getY();
        int x2 = line.getB().getX();
        int y2 = line.getB().getY();

        int incrementValue;

        if(line instanceof LineDotted)
        {
            incrementValue = ((LineDotted) line).getGap();
        }
        else
        {
            incrementValue = 1;
        }

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (Math.abs(dx) > Math.abs(dy)) {
            // Mírný sklon - iterujeme podle X
            if (x1 > x2) {
                // Skutečné prohození souřadnic v lokálních proměnných
                int tempX = x1; x1 = x2; x2 = tempX;
                int tempY = y1; y1 = y2; y2 = tempY;
            }

            double k = (double) (y2 - y1) / (x2 - x1);
            double q = y1 - k * x1;

            for (int x = x1; x <= x2; x+=incrementValue) {
                int y = (int) Math.round(k * x + q);
                raster.setPixel(x, y, color);
//                try {
//                    raster.setPixel(x, y, color);
//                }
//                catch (Exception e)
//                {
//                    throw new RuntimeException("Out of range");
//                }
            }
        } else {
            // Strmý sklon - iterujeme podle Y
            if (y1 > y2) {
                // Skutečné prohození souřadnic
                int tempX = x1; x1 = x2; x2 = tempX;
                int tempY = y1; y1 = y2; y2 = tempY;
            }
            if (y2 != y1) {
                double k_inv = (double) (x2 - x1) / (y2 - y1);
                double q_inv = x1 - k_inv * y1;

                for (int y = y1; y <= y2; y+=incrementValue) {
                    int x = (int) Math.round(k_inv * y + q_inv);
                    raster.setPixel(x, y, color);
                }
            } else {
                // Případ, kdy je čára jen jeden bod
                raster.setPixel(x1, y1, color);
            }
        }
    }

    @Override
    public void rasterize(Ellipse ellipse) {
        int radius = circle.getRadius();

        int x = radius; // Začínáme na pravém okraji (r, 0)
        int y = 0;
        int d = 1 - radius; // Počáteční rozhodovací proměnná
        int x0 = circle.getMidPoint().getX();
        int y0 = circle.getMidPoint().getY();
        while (y <= x) {
            // Vykreslení 8 symetrických bodů kolem středu [x0, y0]
            raster.setPixel(x0 + x, y0 + y, color); // 1. oktant
            raster.setPixel(x0 + y, y0 + x, color); // 2. oktant
            raster.setPixel(x0 - y, y0 + x, color); // 3. oktant
            raster.setPixel(x0 - x, y0 + y, color); // ...
            raster.setPixel(x0 - x, y0 - y, color);
            raster.setPixel(x0 - y, y0 - x, color);
            raster.setPixel(x0 + y, y0 - x, color);
            raster.setPixel(x0 + x, y0 - y, color);

            y++; // V každém kroku jdeme o pixel nahoru

            if (d < 0) {
                // Jsme uvnitř kružnice, jdeme jen doprava
                d += 2 * y + 1;
            } else {
                // Jsme vně, musíme se posunout i dovnitř (zmenšit x)
                x--;
                d += 2 * (y - x) + 1;
            }
        }
    }
}
