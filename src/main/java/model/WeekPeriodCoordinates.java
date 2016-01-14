package model;

import model.Point;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wooll on 02-Jan-16.
 */
public class WeekPeriodCoordinates {

    private OffsetDateTime beginningOfWeek;
    private OffsetDateTime endOfWeek;
    private List<Point> points;

    public WeekPeriodCoordinates(Point point) {
        this.points = new ArrayList<>();
        this.beginningOfWeek = point.getDate();
        this.addPoint(point);
    }

    public WeekPeriodCoordinates() {

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


    //// FIXME: 03-Jan-16 might be a good candidate for an unmodifiable list
    public List<Point> getPoints() {
        return points;
    }
}
