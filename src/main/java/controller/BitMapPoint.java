package controller;

import model.Point;

public class BitMapPoint {

    private Point bitMapPoint;
    private int xIndex;
    private int yIndex;

    BitMapPoint(Point point, int xIndex, int yIndex) {
        this.bitMapPoint = point;
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    public Point getBitMapPoint() {
        return bitMapPoint;
    }

    public int getxIndex() {
        return xIndex;
    }

    public int getyIndex() {
        return yIndex;
    }
}