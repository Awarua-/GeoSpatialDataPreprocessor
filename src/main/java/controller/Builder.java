package controller;

import model.Coordinate;
import model.Point;
import model.WeekPeriodCoordinates;

import java.util.Comparator;
import java.util.List;

import static controller.Util.getCoordinateFromOriginDistanceBearing;
import static controller.Util.searchLatitude;
import static controller.Util.searchLongitude;

public abstract class Builder {
    private final Comparator<Point> latitudeSort = (p1, p2) -> Double.compare(p1.getLatitude(), p2.getLatitude());
    private final Comparator<Point> longitudeSort = (p1, p2) ->Double.compare(p1.getLongitude(), p2.getLongitude());
    private Coordinate origin;
    private double radius;
    private List<WeekPeriodCoordinates> weekPeriodCoordinates;
    private Config config;
    private Coordinate north;
    private Coordinate east;
    private Coordinate south;
    private Coordinate west;

    public Builder(Config config, List<WeekPeriodCoordinates> weekPeriodCoordinates) {
        this.config = config;
        origin = config.getOrigin();
        radius = config.getDistance();
        this.weekPeriodCoordinates = weekPeriodCoordinates;
    }


    List<Point> formBoundaryBox(List<Point> points) {
        north = getCoordinateFromOriginDistanceBearing(origin, radius, 0);
        east = getCoordinateFromOriginDistanceBearing(origin, radius, 270);
        south = getCoordinateFromOriginDistanceBearing(origin, radius, 180);
        west = getCoordinateFromOriginDistanceBearing(origin, radius, 90);

        points.sort(latitudeSort);

        int lowerIndex = searchLatitude(south, points);
        int upperIndex = searchLatitude(north, points);

        points = points.subList(lowerIndex, upperIndex + 1);

        points.sort(longitudeSort);

        upperIndex = searchLongitude(east, points);
        lowerIndex = searchLongitude(west, points);

        return points.subList(lowerIndex, upperIndex + 1);
    }

    Coordinate getNorth() {
        return north;
    }

    Coordinate getEast() {
        return east;
    }

    Coordinate getSouth() {
        return south;
    }

    Coordinate getWest() {
        return west;
    }

    List<WeekPeriodCoordinates> getWeekPeriodCoordinates() {
        return weekPeriodCoordinates;
    }

    abstract void run();

    abstract void calculateBuild(List<Point> points);

    Config getConfig() {
        return config;
    }

    abstract void exportToJSON();
}
