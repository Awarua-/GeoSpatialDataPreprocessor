package model;

import controller.Util;

public class Coordinate {

    private Double longitude;
    private Double latitude;
    private Double longitudeRads;
    private Double latitudeRads;

    public Coordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLongitude() {
        if (longitude == null) {
            longitude = Util.radiansToDegrees(longitudeRads);
        }
        return longitude;
    }

    public Double getLatitude() {
        if (latitude == null) {
            latitude = Util.radiansToDegrees(latitudeRads);
        }
        return latitude;
    }

    public Double getLongitudeRadians() {
        if (longitudeRads == null) {
            longitudeRads = Util.degreesToRadians(longitude);
        }
        return longitudeRads;
    }

    public Double getLatitudeRadians() {
        if (latitudeRads == null) {
            latitudeRads = Util.degreesToRadians(latitude);
        }
        return latitudeRads;
    }
}
