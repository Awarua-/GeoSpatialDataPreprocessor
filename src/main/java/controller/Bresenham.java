package controller;


import java.util.ArrayList;
import java.util.Collection;

public class Bresenham {

    private int realStartY;
    private int realStartX;
    private Collection<Integer[]> points = new ArrayList<>();
    private int maxSectors;
    private int numSectorsPlotted;
    private boolean stop = false;

    public Collection<Integer[]> calculate(BitMapPoint p1, BitMapPoint p2, int maxSectors) {
        int adjustedEndX = p2.getxIndex() - p1.getxIndex();
        int adjustedEndY = p2.getyIndex() - p1.getyIndex();

        int startX = 0;
        int startY = 0;

        realStartX = p1.getxIndex();
        realStartY = p1.getyIndex();

        this.maxSectors = maxSectors;

        int dx = adjustedEndX - startX;
        int dy = adjustedEndY - startY;

        if (dx > dy && dy >= 0) {
            bresenham(startX, startY, adjustedEndX, adjustedEndY, 0);
        }
        else if (dy >= dx && dx >= 0) {
            bresenham(startX, startY, adjustedEndY, adjustedEndX, 1);
        }
        else if (dx < 0 && dy > 0 && dy >= Math.abs(dx)) {
            bresenham(startX, startY, adjustedEndY, -1 * adjustedEndX, 2);
        }
        else if (dx < 0 && dy >= 0 && Math.abs(dx) > dy) {
            bresenham(startX, startY, -1 * adjustedEndX, adjustedEndY, 3);
        }
        else if (dx < 0 && dy < 0 && Math.abs(dx) >= Math.abs(dy)) {
            bresenham(startX, startY, -1 * adjustedEndX, -1 * adjustedEndY, 4);
        }
        else if (dx <= 0 && dy < 0 && Math.abs(dy) > Math.abs(dx)) {
            bresenham(startX, startY, -1 * adjustedEndY, -1 * adjustedEndX, 5);
        }
        else if (dy < 0 && dx > 0 && Math.abs(dy) >= dx) {
            bresenham(startX, startY, -1 * adjustedEndY, adjustedEndX, 6);
        }
        else if(dy < 0 && dx > 0 && dx > Math.abs(dy)) {
            bresenham(startX, startY, adjustedEndX, -1 * adjustedEndY, 7);
        }

        if (stop) {
            points.clear();
        }

        return points;
    }

    private void bresenham(int x1, int y1, int x2, int y2, int octant) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int f = 2 * dy - dx;
        boolean first = true;

        while (x1 <= x2 && y1 <= y2 && !stop) {
            if (!first) {
                plot(x1, y1, octant);
            }
            else {
                first = false;
            }

            if (f < 0) {
                f += 2 * dy;
            }
            else {
                f += 2 * (dy - dx);
                y1 += 1;
            }
            x1 += 1;
        }
    }

    private void plot(int x1, int y1, int octant) {
        int x = 0;
        int y = 0;
        if (++numSectorsPlotted > maxSectors) {
            stop = true;
            return;
        }
        switch (octant) {
            case 0:
                x = x1 + realStartX;
                y = y1 + realStartY;
                break;
            case 1:
                x = y1 + realStartX;
                y = x1 + realStartY;
                break;
            case 2:
                x = -1 * y1 + realStartX;
                y = x1 + realStartY;
                break;
            case 3:
                x = -1 * x1 + realStartX;
                y = y1 + realStartY;
                break;
            case 4:
                x = -1 * x1 + realStartX;
                y = -1 * y1 + realStartY;
                break;
            case 5:
                x = -1 * y1 + realStartX;
                y = -1 * x1 + realStartY;
                break;
            case 6:
                x = y1 + realStartX;
                y = -1 * x1 + realStartY;
                break;
            case 7:
                x = x1 + realStartX;
                y = -1 * y1 + realStartY;
                break;
        }
        Integer[] thing = new Integer[2];
        thing[0] = x;
        thing[1] = y;
        points.add(thing);
    }
}
