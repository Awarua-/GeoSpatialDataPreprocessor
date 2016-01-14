package model;

import java.time.OffsetDateTime;
import java.util.List;

public class KDTree {

    private List<Quadrant> quadrants;
    private int treeDepth;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    public KDTree(List<Quadrant> quadrants, int depth) {
        this.quadrants = quadrants;
        this.treeDepth = depth;
    }

    public List<Quadrant> getQuadrants() {
        return quadrants;
    }

    public int getTreeDepth() {
        return treeDepth;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }
}
