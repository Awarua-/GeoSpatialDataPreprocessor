package model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class WeekPeriodCoordinates {

    private OffsetDateTime beginningOfWeek;
    private OffsetDateTime endOfWeek;
    private List<Point> points;

    public WeekPeriodCoordinates(Point point) {
        this.points = new ArrayList<>();
        this.beginningOfWeek = point.getDate();
        this.addPoint(point);
    }

    public WeekPeriodCoordinates(List<Point> points) {
        this.points = points;
        this.beginningOfWeek = points.get(0).getDate();
        this.endOfWeek = points.get(points.size() -1).getDate();
    }

    public void addPoint(Point point) {
        points.add(point);
        endOfWeek = point.getDate();
    }

    public OffsetDateTime getBeginningOfWeek() {
        return beginningOfWeek;
    }

    public OffsetDateTime getEndOfWeek() {
        return endOfWeek;
    }

    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }
}
