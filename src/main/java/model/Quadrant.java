package model;

import java.math.BigInteger;
import java.util.Collection;

public class Quadrant implements Comparable<Quadrant> {

    private BigInteger id;
    private Collection<Point> points;
    private Coordinate origin;
    private Double radius;

    public Quadrant(BigInteger id, Collection<Point> points, Coordinate origin, double radius) {
        this.id = id;
        this.points = points;
        this.origin = origin;
        this.radius = radius;
    }

    BigInteger getId() {
        return id;
    }

    Collection<Point> getPoints() {
        return points;
    }

    Double getRadius() {
        return radius;
    }

    Coordinate getOrigin() {
        return origin;
    }

    @Override
    public int compareTo(Quadrant q) {
        return this.getId().compareTo(q.getId());
    }

    @Override
    public String toString() {
        return origin.getLatitude() + " " + origin.getLongitude();
    }
}
