package controller;

import model.*;

import java.math.BigInteger;
import java.util.*;
import java.lang.Math;

import static controller.Util.getCoordinateFromOriginDistanceBearing;
import static controller.Util.searchLatitude;
import static controller.Util.searchLongitude;


public class QuadTreeBuilder {

    private List<Point> points;
    private final Comparator<Point> latitudeSort;
    private final Comparator<Point> longitudeSort;
    private final Double distance = 50000.0;
    private final Coordinate origin;
    private int maxDepth = (int) (Math.log(distance / 10) / Math.log(2));
    // Accuracy to under 10m x 10m area
    // NB: The number of quadrants generated is equal to the geometric series of 1 - 4 ^ (maxDepth +1) / 3
    // Highest realistic value is 1/2 the circumference of the earth which has a depth of 26 so max num quadrants = 1,501,199,875,790,165
    // Optimisations made,
    //      Only calculate points once
    //      alternate sorted points
    //      Exit prematurely when no points left to calculate in quadrant

    private List<Quadrant> quadrants;


    public QuadTreeBuilder(List<Point> points) {
        this.points = points;

        latitudeSort = (p1, p2) -> Double.compare(p1.getLatitude(), p2.getLatitude());
        longitudeSort = (p1, p2) ->Double.compare(p1.getLongitude(), p2.getLongitude());
        origin = new Coordinate(-36.852840, 174.762955);
        quadrants = new ArrayList<>();
    }

    QuadTree run() {
        System.out.println("running");
        Coordinate north = getCoordinateFromOriginDistanceBearing(origin, distance, 0);
        Coordinate east = getCoordinateFromOriginDistanceBearing(origin, distance, 90);
        Coordinate south = getCoordinateFromOriginDistanceBearing(origin, distance, 180);
        Coordinate west = getCoordinateFromOriginDistanceBearing(origin, distance, 270);

        points.sort(latitudeSort);

        int lowerIndex = searchLatitude(south, points);
        int upperIndex = searchLatitude(north, points);

        points = points.subList(lowerIndex, upperIndex + 1);

        points.sort(longitudeSort);

        upperIndex = searchLongitude(west, points);
        lowerIndex = searchLongitude(east, points);

        points = points.subList(lowerIndex, upperIndex + 1);

        try {
            recurDown(distance, origin, BigInteger.valueOf(0), points, false, 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new QuadTree(quadrants, maxDepth);
    }

    private void northWestQuadrantSearch(Coordinate south, Coordinate east, double distance, BigInteger quadrant, List<Point> points, boolean sortedByLat, int depth) {
        if (depth > maxDepth) return;

        Coordinate origin = getCoordinateFromOriginDistanceBearing(south, distance, 0);
        if (sortedByLat) {
            int lowerIndex = searchLatitude(south, points);
            int upperIndex = points.size();

            points = points.subList(lowerIndex, upperIndex);

            points.sort(longitudeSort);
            sortedByLat = false;

            lowerIndex = searchLongitude(east, points);
            upperIndex = points.size();

            points = points.subList(lowerIndex, upperIndex);

        }
        else {
            int lowerIndex = searchLongitude(east, points);
            int upperIndex = points.size();

            points = points.subList(lowerIndex, upperIndex);

            points.sort(latitudeSort);
            sortedByLat = true;

            lowerIndex = searchLatitude(south, points);
            upperIndex = points.size();

            points = points.subList(lowerIndex, upperIndex);
        }

        recurDown(distance, origin, quadrant, points, sortedByLat, depth);
    }

    private void southWestQuadrantSearch(Coordinate north, Coordinate east, double distance, BigInteger quadrant, List<Point> points, boolean sortedByLat, int depth) {
        if (depth > maxDepth) return;
        Coordinate origin = getCoordinateFromOriginDistanceBearing(north, distance, 180);

        if (sortedByLat) {
            int lowerIndex = 0;
            int upperIndex = searchLatitude(north, points);

            points = points.subList(lowerIndex, upperIndex);

            points.sort(longitudeSort);
            sortedByLat = false;

            lowerIndex = searchLongitude(east, points);
            upperIndex = points.size();

            points = points.subList(lowerIndex, upperIndex);

        }
        else {
            int lowerIndex = searchLongitude(east, points);
            int upperIndex = points.size();

            points = points.subList(lowerIndex, upperIndex);

            points.sort(latitudeSort);
            sortedByLat = true;

            lowerIndex = 0;
            upperIndex = searchLatitude(north, points);

            points = points.subList(lowerIndex, upperIndex);
        }
        recurDown(distance, origin, quadrant, points, sortedByLat, depth);
    }

    private void northEastQuadrantSearch(Coordinate south, Coordinate west, double distance, BigInteger quadrant, List<Point> points, boolean sortedByLat, int depth) {
        if (depth > maxDepth) return;
        Coordinate origin = getCoordinateFromOriginDistanceBearing(south, distance, 0);

        if (sortedByLat) {
            int lowerIndex = searchLatitude(south, points);
            int upperIndex = points.size();

            points = points.subList(lowerIndex, upperIndex);

            points.sort(longitudeSort);
            sortedByLat = false;

            lowerIndex = 0;
            upperIndex = searchLongitude(west, points);

            points = points.subList(lowerIndex, upperIndex);

        }
        else {
            int lowerIndex = 0;
            int upperIndex = searchLongitude(west, points);

            points = points.subList(lowerIndex, upperIndex);

            points.sort(latitudeSort);
            sortedByLat = true;

            lowerIndex = searchLatitude(south, points);
            upperIndex = points.size();

            points = points.subList(lowerIndex, upperIndex);
        }
        recurDown(distance, origin, quadrant, points, sortedByLat, depth);
    }

    private void southEastQuadrantSearch(Coordinate north, Coordinate west, double distance, BigInteger quadrant, List<Point> points, boolean sortedByLat, int depth) {
        if (depth > maxDepth) return;
        Coordinate origin = getCoordinateFromOriginDistanceBearing(north, distance, 180);

        if (sortedByLat) {
            int lowerIndex = 0;
            int upperIndex = searchLatitude(north, points);

            points = points.subList(lowerIndex, upperIndex);

            points.sort(longitudeSort);
            sortedByLat = false;

            lowerIndex = 0;
            upperIndex = searchLongitude(west, points);

            points = points.subList(lowerIndex, upperIndex);

        }
        else {
            int lowerIndex = 0;
            int upperIndex = searchLongitude(west, points);

            points = points.subList(lowerIndex, upperIndex);

            points.sort(latitudeSort);
            sortedByLat = true;

            lowerIndex = 0;
            upperIndex = searchLatitude(north, points);

            points = points.subList(lowerIndex, upperIndex);
        }
        recurDown(distance, origin, quadrant, points, sortedByLat, depth);
    }

    private void recurDown(double distance, Coordinate origin, BigInteger quadrant, List<Point> points, boolean sortedByLat, int depth) {
        quadrants.add(new Quadrant(quadrant, points, origin, distance));
        if (points.size() <= 0) {
            return;
        }

        double halfDistance = distance / 2;
        Coordinate halfNorth = getCoordinateFromOriginDistanceBearing(origin, halfDistance, 0);
        Coordinate halfEast = getCoordinateFromOriginDistanceBearing(origin, halfDistance, 90);
        Coordinate halfSouth = getCoordinateFromOriginDistanceBearing(origin, halfDistance, 180);
        Coordinate halfWest = getCoordinateFromOriginDistanceBearing(origin, halfDistance, 270);

        BigInteger four = BigInteger.valueOf(4);

        northWestQuadrantSearch(halfWest, halfNorth, halfDistance, quadrant.multiply(four).add(BigInteger.valueOf(1)), points, sortedByLat, depth + 1);
        southWestQuadrantSearch(halfWest, halfSouth, halfDistance, quadrant.multiply(four).add(BigInteger.valueOf(2)), points, sortedByLat, depth + 1);
        northEastQuadrantSearch(halfEast, halfNorth, halfDistance, quadrant.multiply(four).add(BigInteger.valueOf(3)), points, sortedByLat, depth + 1);
        southEastQuadrantSearch(halfEast, halfSouth, halfDistance, quadrant.multiply(four).add(BigInteger.valueOf(4)), points, sortedByLat, depth + 1);
    }
}
