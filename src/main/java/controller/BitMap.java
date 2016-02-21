package controller;

import model.Coordinate;


class BitMap {
    private int count = 0;
    private Coordinate center;
    private double radius;
    private Coordinate coordinate;

    BitMap(Coordinate coordinate, double radius) {
        this.radius = radius;
        this.coordinate = coordinate;
    }

    int increment() {
        return count++;
    }

    public double getRadius() {
        return radius;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getCount() {
        return count;
    }
}
