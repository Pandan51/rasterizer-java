package math;
import models.Point;


public class AngleCalculator {


    public static double getAngle(Point A, Point B){

        int x_A = A.getX();
        int x_B = B.getX();

        int y_A = A.getY();
        int y_B = B.getY();

        int x_diff = Math.abs(x_A - x_B);
        int y_diff = Math.abs(y_A - y_B);

        double angleRad = Math.atan2(y_diff, x_diff);
        double angleDeg = Math.toDegrees(angleRad);

        return angleDeg;
    }

    public static Point getSnappedB(Point A, Point B, double angle){
        int x_A = A.getX();
        int x_B = B.getX();

        int y_A = A.getY();
        int y_B = B.getY();

        int x_diff = x_B - x_A;
        int y_diff = y_B - y_A;

        Point newPoint;

        if(angle < 22.5)
        {
            newPoint = new Point(x_B, y_A);
        }

        else if(angle > 22.5 && angle < 77.5) {
            int sideLength = Math.toIntExact(Math.round((double) (Math.abs(x_diff) + Math.abs(y_diff)) / 2));

            int newX_B = x_B > x_A ? x_A + sideLength : x_A - sideLength;
            int newY_B = y_B > y_A ? y_A + sideLength : y_A - sideLength;

            newPoint = new Point(newX_B, newY_B);
        }
//        if(angle > 77.5)
        else
        {
            newPoint = new Point(x_A, y_B);
        }
//        if(angle % 90 == 0)
//        {
//            Point newPoint = new Point(x_A + (x_B - x_A), y_A + (y_B - y_A));
//
//            return newPoint;
//        }
//        else{
//
//        }

        return newPoint;
    }

    public static Point getSnappedPoint(Point A, Point B)
    {
        double angle = getAngle(A,B);
        return getSnappedB(A,B,angle);
    }
}
