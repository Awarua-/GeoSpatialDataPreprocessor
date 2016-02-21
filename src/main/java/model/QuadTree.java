package model;

import java.time.OffsetDateTime;
import java.util.List;

public class QuadTree {

    private List<Quadrant> quadrants;
    private int treeDepth;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    public QuadTree(List<Quadrant> quadrants, int depth) {
        this.quadrants = quadrants;
        this.treeDepth = depth;
    }

    List<Quadrant> getQuadrants() {
        return quadrants;
    }

    int getTreeDepth() {
        return treeDepth;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    OffsetDateTime getStartDate() {
        return startDate;
    }

    OffsetDateTime getEndDate() {
        return endDate;
    }
}
