package models;

public class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Přidáno pro možnost posouvání bodů
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
