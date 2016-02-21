package controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Coordinate;
import model.Point;
import model.WeekPeriodCoordinates;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitMapBuilder extends Builder {

    private final Gson gson;
    private ConcurrentHashMap<Integer, BitMap> bitMap;
    private Integer axisLength;
    private ExecutorService executors;
    private Integer maxSectors;

    public BitMapBuilder(Config config, List<WeekPeriodCoordinates> weekPeriodCoordinates) {
        super(config, weekPeriodCoordinates);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(OutputBitMap.class, new BitMapSerializer());
        gson = gsonBuilder.create();
    }

    @Override
    void run() {
        for (WeekPeriodCoordinates weekCoordinates : getWeekPeriodCoordinates()) {
            calculateBuild(weekCoordinates.getPoints());
            System.out.println("before json");
            OutputBitMap output = new OutputBitMap(bitMap.values(), weekCoordinates.getBeginningOfWeek(), weekCoordinates.getEndOfWeek());
            String json = gson.toJson(output, OutputBitMap.class);
            System.out.println("After json");
            try {
                FileWriter fileWriter = new FileWriter("b" + weekCoordinates.getBeginningOfWeek().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + weekCoordinates.getEndOfWeek().format(DateTimeFormatter.BASIC_ISO_DATE) + ".json");
                fileWriter.write(json);
                fileWriter.flush();
                fileWriter.close();
                System.out.println("file done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    void calculateBuild(List<Point> points) {
        points = formBoundaryBox(points);
        Map<Integer, List<Point>> pointsByUserId = new HashMap<>();
        List<Point> userPoints;
        System.out.println("Grouping points by user");
        for (Point point : points) {
            userPoints = pointsByUserId.get(point.getUserId());
            if (userPoints == null) {
                userPoints = new ArrayList<>();
                userPoints.add(point);
                pointsByUserId.put(point.getUserId(), userPoints);
            }
            else {
                userPoints.add(point);
                pointsByUserId.put(point.getUserId(), userPoints);
            }
        }
        System.out.println("Building bitmap structure");
        buildBitMap();

        List<Runnable> work = new ArrayList<>();

        System.out.println("Begin calculating paths for: " + pointsByUserId.entrySet().size() + " users");
        for (Map.Entry<Integer, List<Point>> entry : pointsByUserId.entrySet()) {
            executors.execute(new Builder(entry.getValue(), bitMap));
        }
        executors.shutdown();
    }

    @Override
    void exportToJSON() {

    }

    private class Builder implements Runnable {

        private final Map<Integer, BitMap> map;
        private List<Point> pointsToProcess;
        private Bresenham bresenham;
        private Coordinate zeroCoordinate;
        private Double xStep;
        private Double yStep;

        Builder(List<Point> points, Map<Integer, BitMap> map) {
            this.pointsToProcess = points;
            this.map = map;
            bresenham = new Bresenham();
        }

        @Override
        public void run() {
            buildPath();
        }

        private void buildPath() {
            // Sort points in ascending date/time order
            pointsToProcess.sort((p1, p2) -> p1.getDate().compareTo(p2.getDate()));
            BitMapPoint currentPoint = calculateBitMapPoint(pointsToProcess.get(0));
            BitMapPoint nextPoint = null;
            BitMap thing = bitMap.get(currentPoint.getxIndex() * findSectorsPerAxis() + currentPoint.getyIndex());
            if (thing == null) {
                thing = addPoint(currentPoint.getxIndex(), currentPoint.getyIndex());
            }
            thing.increment();
            Collection<Integer[]> points;
            for (int i = 1; i < pointsToProcess.size(); i++) {
                nextPoint = calculateBitMapPoint(pointsToProcess.get(i));
                Duration duration = Duration.between(currentPoint.getBitMapPoint().getDate(), nextPoint.getBitMapPoint().getDate());
                if (duration.toHours() < 1) {
                    points = bresenham.calculate(currentPoint, nextPoint, getMaxTravelSectors());
                    plot(points);
                    currentPoint = nextPoint;
                }
            }
        }

        private void plot(Collection<Integer[]> points) {
            BitMap thing = null;
            for (Integer[] point: points) {
                thing = bitMap.get(point[0] * findSectorsPerAxis() + point[1]);
                if (thing == null) {
                    thing = addPoint(point[0], point[1]);
                }
                thing.increment();
            }
        }

        private BitMap addPoint(int x, int y) {
//            Coordinate xCoordinate = Util.getCoordinateFromOriginDistanceBearing(getBitMapZeroCoordinate(), (x * getConfig().getResolution()), 270);
//            Coordinate xYCoordinate = Util.getCoordinateFromOriginDistanceBearing(xCoordinate, (y * getConfig().getResolution()), 0);
            Coordinate xYCoordinate = new Coordinate(getBitMapZeroCoordinate().getLatitude() + (y * getYStep() + getYStep() / 2), getBitMapZeroCoordinate().getLongitude() + (x * getXStep() + getXStep() / 2));
            BitMap thing = new BitMap(xYCoordinate, getConfig().getResolution());
            bitMap.put(x * findSectorsPerAxis() + y, thing);
            return thing;
        }

        private BitMapPoint calculateBitMapPoint(Point point) {
            Double deltaX = Math.abs(getBitMapZeroCoordinate().getLongitude() - point.getCoordinate().getLongitude());
            Double deltaY = Math.abs(getBitMapZeroCoordinate().getLatitude() - point.getCoordinate().getLatitude());
            //Get rounded up integer index between 0 and axisLength
            int xIndex = (int) Math.max(Math.min(Math.floor(deltaX / getXStep()), findSectorsPerAxis() - 1), 0);
            int yIndex = (int) Math.max(Math.min(Math.floor(deltaY / getYStep()), findSectorsPerAxis() - 1), 0);
            return new BitMapPoint(point, xIndex, yIndex);
        }

        private double getXStep() {
            if (xStep == null) {
                xStep = Math.abs((getWest().getLongitude() - getEast().getLongitude()) / findSectorsPerAxis());
            }
            return xStep;
        }

        private double getYStep() {
            if (yStep == null) {
                yStep = Math.abs((getSouth().getLatitude() - getNorth().getLatitude()) / findSectorsPerAxis());
            }
            return yStep;
        }

        private Coordinate getBitMapZeroCoordinate() {
            if (zeroCoordinate == null) {
                zeroCoordinate = Util.getCoordinateFromOriginDistanceBearing(getSouth(), getConfig().getDistance(), 90);
            }
            return zeroCoordinate;
        }
    }



    private void buildBitMap() {
        executors = Executors.newCachedThreadPool();
        if (bitMap == null) {
            bitMap = new ConcurrentHashMap<>(findSectorsPerAxis());
        }
        else {
            bitMap.clear();
            System.gc();
        }
    }

    private int findSectorsPerAxis() {
        if (axisLength ==  null) {
            axisLength = (int) (getConfig().getDistance() / getConfig().getResolution());
        }
        return axisLength;
    }

    private int getMaxTravelSectors() {
        if (maxSectors == null) {
            maxSectors =  (int) (getConfig().getMaxPathDistance() / getConfig().getResolution());
        }
        return maxSectors;
    }


}
