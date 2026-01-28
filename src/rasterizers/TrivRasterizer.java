package rasterizers;
import models.Line;
import models.LineDotted;
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

//        double k = (line.getB().getY() - line.getA().getY()) / (double)(line.getB().getX() - line.getA().getX());
//        double q = line.getA().getY() - k * line.getA().getX();
//
//        // TODO vyřešit hranice okna
//        // TODO vyřešit svislou úsečku
//
//
//        if(k < 1){
//            // TODO prohodit body pokud je potřeba
//            int min = Math.min(line.getA().getX(), line.getB().getX());
//            int max = Math.max(line.getA().getX(), line.getB().getX());
//
//
//
//
//            for (int i = min; i < max; i++) {
//                // Výpočet Y
//                int t = (int) Math.round(k * i + q);
//
//                raster.setPixel(i, t, color);
//            }
//        }
//        else{
//            // TODO prohodit body, pokud jte potřeba
//
//            int min = Math.min(line.getA().getY(), line.getB().getY());
//            int max = Math.max(line.getA().getY(), line.getB().getY());
//
//
//
//            for (int i = min; i < max; i++) {
//                // Výpočet X
//                int t = (int) Math.round((i - q)/k);
//
//                raster.setPixel(t, i, color);
//            }
//        }
//
//
//        double dx = line.getB().getX() - line.getA().getX();
//        double dy = line.getB().getY() - line.getA().getY();
//
//        if (Math.abs(dx) > Math.abs(dy)) {
//            // Mírný sklon (iterujeme podle X)
//
//            // Zajistíme, aby A bylo vlevo a B vpravo
//            if (line.getA().getX() > line.getB().getX()) {
//                // swap bodů (pomocí pomocné proměnné)
//
//            }
//
//            double k = (double) (line.getB().getY() - line.getA().getY()) / (line.getB().getX() - line.getA().getX());
//            double q = line.getA().getY() - k * line.getA().getX();
//
//            for (int x = line.getA().getX(); x <= line.getB().getX(); x++) {
//                int y = (int) Math.round(k * x + q);
//                raster.setPixel(x, y, color);
//            }
//        } else {
//            // Strmý sklon (iterujeme podle Y)
//
//            // Zajistíme, aby A bylo nahoře a B dole
//            if (line.getA().getY() > line.getB().getY()) {
//                // swap bodů
//            }
//
//            // Pozor: pokud je čára svislá, dx je 0. Výpočet k = dy/dx by vyhodil chybu.
//            // Proto v této větvi počítáme převrácenou směrnici (dx/dy).
//            double k_inv = (double) (line.getB().getX() - line.getA().getX()) / (line.getB().getY() - line.getA().getY());
//            double q_inv = line.getA().getX() - k_inv * line.getA().getY();
//
//            for (int y = line.getA().getY(); y <= line.getB().getY(); y++) {
//                int x = (int) Math.round(k_inv * y + q_inv);
//                raster.setPixel(x, y, color);
//            }
//        }

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
                try {
                    raster.setPixel(x, y, color);
                }
                catch (Exception e)
                {

                }
            }
        } else {
            // Strmý sklon - iterujeme podle Y
            if (y1 > y2) {
                // Skutečné prohození souřadnic
                int tempX = x1; x1 = x2; x2 = tempX;
                int tempY = y1; y1 = y2; y2 = tempY;
            }

            // Kontrola pro svislou čáru (dy by bylo 0, ale to díky if (abs(dy) >= abs(dx)) nenastane,
            // leda by body byly totožné)
            if (y2 != y1) {
                double k_inv = (double) (x2 - x1) / (y2 - y1);
                double q_inv = x1 - k_inv * y1;

                for (int y = y1; y <= y2; y+=incrementValue) {
                    int x = (int) Math.round(k_inv * y + q_inv);
//                    raster.setPixel(x, y, color);
                    try {
                        raster.setPixel(x, y, color);
                    }
                    catch (Exception e)
                    {
                        break;
                    }
                }
            } else {
                // Případ, kdy je čára jen jeden bod
                raster.setPixel(x1, y1, color);
            }
        }
    }
}
