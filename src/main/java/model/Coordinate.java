package model;

import controller.Util;

/**
 * Represents a geographical coordinate in many forms.
 */
public class Coordinate {

    /**
     * Longitude in degrees.
     */
    private Double longitude;

    /**
     * Latitude in degrees.
     */
    private Double latitude;

    /**
     * Longitude in radians.
     */
    private Double longitudeRads;

    /**
     * Latitude in radians.
     */
    private Double latitudeRads;

    /**
     * Cartesian coordinates.
     */
    private CartesianCoordinate cartesianCoordinate;

    /**
     * Creates a new coordinate.
     *
     * @param latitude  in degrees.
     * @param longitude in degrees.
     */
    public Coordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the longitude in degrees.
     *
     * @return longitude in degrees.
     */
    public Double getLongitude() {
        if (longitude == null) {
            longitude = Util.radiansToDegrees(longitudeRads);
        }
        return longitude;
    }

    /**
     * Gets the latitude in degrees.
     *
     * @return latitude in degrees.
     */
    public Double getLatitude() {
        if (latitude == null) {
            latitude = Util.radiansToDegrees(latitudeRads);
        }
        return latitude;
    }

    /**
     * Gets the longitude in radians.
     *
     * @return longitude in radians.
     */
    public Double getLongitudeRadians() {
        if (longitudeRads == null) {
            longitudeRads = Util.degreesToRadians(longitude);
        }
        return longitudeRads;
    }

    /**
     * Gets the latitude in radians.
     *
     * @return latitude in radians
     */
    public Double getLatitudeRadians() {
        if (latitudeRads == null) {
            latitudeRads = Util.degreesToRadians(latitude);
        }
        return latitudeRads;
    }

    /**
     * Gets the cartesian coordinate for this coordinate
     *
     * @return a cartesian Coordinate
     */
    public CartesianCoordinate getCartesianCoordinates() {
        if (this.cartesianCoordinate == null) {
            Double latitude = getLatitudeRadians();
            Double longitude = getLongitudeRadians();

            if (latitude == null || longitude == null) {
                throw new RuntimeException("Cant compute cartesian coordinate of invalid coordinate");
            }

            this.cartesianCoordinate = Util.getCartesianCoordinate(latitude, longitude);
            return this.cartesianCoordinate;
        } else return this.cartesianCoordinate;
    }
}
