package model;

public class CartesianCoordinate {

    private Double x;
    private Double y;
    private Double z;

    public CartesianCoordinate(Double x, Double y) {
        new CartesianCoordinate(x, y, null);
    }

    public CartesianCoordinate(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }
}
