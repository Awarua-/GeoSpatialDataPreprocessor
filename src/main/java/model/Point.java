package model;

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
    private Coordinate coordinate;

    public Point(String id, String userId, String latitude, String longitude, String date) {
        this.id = Integer.parseInt(id);
        this.userId = Integer.parseInt(userId);
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.coordinate = new Coordinate(this.latitude, this.longitude);
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

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
