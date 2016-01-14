package model;

import controller.Util;
import loader.GeoZoneTables;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


public class Point {

    @GeoZoneTables(columnName = "id")
    private int id;
    @GeoZoneTables(columnName = "user_id")
    private int userId;
    @GeoZoneTables(columnName = "latitude")
    private double latitude;
    @GeoZoneTables(columnName = "longitude")
    private double longitude;
    @GeoZoneTables(columnName = "created_at")
    private OffsetDateTime date;
    private Double latitudeRads;
    private Double longitudeRads;

    public Point(String id, String userId, String latitude, String longitude, String date) {
        this.id = Integer.parseInt(id);
        this.userId = Integer.parseInt(userId);
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        try {
            String timeZonedDate = date.replaceAll("UTC", "+0000");
            this.date = OffsetDateTime.parse(timeZonedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUserId() {
        return userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public Double getLatitudeRads() {
        if (latitudeRads == null) {
            latitudeRads = Util.radiansToDegrees(latitude);
        }
        return latitudeRads;
    }

    public Double getLongitudeRads() {
        if (longitudeRads == null) {
            longitudeRads = Util.radiansToDegrees(longitude);
        }
        return longitudeRads;
    }
}
