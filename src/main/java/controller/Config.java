package controller;

import model.Coordinate;

public class Config {

    private double latitude;
    private double longitude;
    private double distance;
    private double resolution;
    private Coordinate origin;
    private double maxPathDistance;

    public Config(double latitude, double longitude, double distance, double resolution, double maxPathDistance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.resolution = resolution;
        this.origin = new Coordinate(latitude, longitude);
        this.maxPathDistance = maxPathDistance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public double getResolution() {
        return resolution;
    }

    public Coordinate getOrigin() {
        if (origin == null) {
            origin = new Coordinate(latitude, longitude);
        }
        return origin;
    }

    public double getMaxPathDistance() {
        return maxPathDistance;
    }
}
