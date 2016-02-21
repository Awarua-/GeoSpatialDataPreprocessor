package controller;

import model.CartesianCoordinate;
import model.Coordinate;
import model.Point;

import java.util.List;

/**
 * Utility methods used in several places.
 */
public class Util {

    /**
     * Multiplier to convert between radians and degrees.
     */
    private static final Double RADIANS_TO_DEGREES_MULTIPLIER = 180 / Math.PI;
    /**
     * Multiplier to convert between degrees and radians.
     */
    private static final Double DEGREES_TO_RADIANS_MULTIPLIER = Math.PI / 180;
    /**
     * Approximation of the Earths radius.
     */
    private static final Double EARTH_RADIUS = 6366710.0;

    /**
     * Converts radians to degrees.
     *
     * @param radian to convert to degree
     * @return degree
     */
    public static Double radiansToDegrees(Double radian) {
        if (radian == null) {
            return null;
        }
        return radian * RADIANS_TO_DEGREES_MULTIPLIER;
    }

    /**
     * Converts degree to radian
     *
     * @param degree to convert to radian
     * @return radian
     */
    public static Double degreesToRadians(Double degree) {
        if (degree == null) {
            return null;
        }
        return degree * DEGREES_TO_RADIANS_MULTIPLIER;
    }

    /**
     * Gets a new coordinate starting from an origin point and a distance and bearing vector.
     *
     * @param origin   starting point
     * @param distance in meters
     * @param bearing  in degrees
     * @return coordinate
     */
    static Coordinate getCoordinateFromOriginDistanceBearing(Coordinate origin, double distance, double bearing) {
        double distanceInRadians = distance / EARTH_RADIUS;
        double tc = Util.degreesToRadians(bearing);
        double latitudeRadians = origin.getLatitudeRadians();
        double longitudeRadians = origin.getLongitudeRadians();
        double sinLatitude = Math.sin(latitudeRadians);
        double newlat = Math.asin((sinLatitude * Math.cos(distanceInRadians)) + (Math.cos(latitudeRadians) * Math.sin(distanceInRadians) * Math.cos(tc)));
        double dlon = Math.atan2((Math.sin(tc) * Math.sin(distanceInRadians) * Math.cos(latitudeRadians)), (Math.cos(distanceInRadians) - (sinLatitude * sinLatitude)));
        double newlong = ((longitudeRadians - dlon + Math.PI) % (2 * Math.PI)) - Math.PI;
        return new Coordinate(Util.radiansToDegrees(newlat), Util.radiansToDegrees(newlong));
    }

    /**
     * Search a list of points for the closet point to a given value.
     * Assumes that the list of points are sorted by latitude.
     *
     * @param value  The value to search for
     * @param points List of points sorted by latitude.
     * @return index of point closest to value.
     */
    static int searchLatitude(Coordinate value, List<Point> points) {
        if (points.size() <= 0) {
            return 0;
        }
        int low = 0;
        int high = points.size() - 1;
        int mid;
        Double midValue;

        while (low < high) {
            mid = low + ((high - low) / 2);
            midValue = points.get(mid).getLatitude();
            if (midValue < value.getLatitude()) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        if (high == low) {
            return low;
        } else {
            return -1;
        }
    }

    /**
     * Search a list of points for the closet point to a given value.
     * Assumes that the list of points are sorted by longitude.
     *
     * @param value  The value to search for
     * @param points List of points sorted by longitude.
     * @return index of point closest to value.
     */
    static int searchLongitude(Coordinate value, List<Point> points) {
        if (points.size() <= 0) {
            return 0;
        }
        int low = 0;
        int high = points.size() - 1;
        int mid;
        Double midValue;

        while (low < high) {
            mid = low + ((high - low) / 2);
            midValue = points.get(mid).getLongitude();
            if (midValue < value.getLongitude()) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        if (high == low) {
            return low;
        } else {
            return -1;
        }
    }

    /**
     * Creates a cartesian from latitude and longitude
     *
     * @param latitude  Latitude in radians
     * @param longitude Longitude in radian
     * @return a cartesian coordinate
     */
    public static CartesianCoordinate getCartesianCoordinate(Double latitude, Double longitude) {
        Double x = EARTH_RADIUS * Math.cos(latitude) * Math.cos(longitude);
        Double y = EARTH_RADIUS * Math.cos(latitude) * Math.sin(longitude);
        Double z = EARTH_RADIUS * Math.sin(latitude);

        return new CartesianCoordinate(x, y, z);
    }

    public static Coordinate convertCartesianCoordinate(Double x, Double y, Double z) {
        Double latitude = Math.asin(z / EARTH_RADIUS);
        Double longitude = Math.atan2(y, x);
        return new Coordinate(radiansToDegrees(latitude), radiansToDegrees(longitude));
    }
}
