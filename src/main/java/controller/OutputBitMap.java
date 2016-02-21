package controller;

import java.time.OffsetDateTime;
import java.util.Collection;

public class OutputBitMap {
    private Collection<BitMap> points;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    public OutputBitMap(Collection<BitMap> points, OffsetDateTime startDate, OffsetDateTime endDate) {
        this.points = points;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    Collection<BitMap> getPoints() {
        return points;
    }

    OffsetDateTime getStartDate() {
        return startDate;
    }

    OffsetDateTime getEndDate() {
        return endDate;
    }
}
