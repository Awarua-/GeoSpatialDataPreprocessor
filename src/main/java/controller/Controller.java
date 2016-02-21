package controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import loader.Loader;
import loader.LoaderImpl;
import model.Point;
import model.QuadTree;
import model.QuadTreeSerializer;
import model.WeekPeriodCoordinates;

import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    private String[] files;
    private List<Point> points;
    private Callback callback;
    private int numFilesLoaded;
    private QuadTreeBuilder kdSearch;
    private ExecutorService fileService;
    private ExecutorService QuadTreeBuilderService;
    private WeekFields wf = WeekFields.of(Locale.ENGLISH);
    private List<WeekPeriodCoordinates> weekPeriodCoordinates;
    private int searchesComplete;
    private Gson gson;
    private Config config;

    public Controller(String[] files) {
        this.files = files;
        numFilesLoaded = 0;
        searchesComplete = 0;
        points = Collections.synchronizedList(new ArrayList<>());
        callback = this::checkFilesLoaded;
        fileService = Executors.newFixedThreadPool(files.length);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(QuadTree.class, new QuadTreeSerializer());
        gson = gsonBuilder.create();
        config = ConfigLoader.getInstance().getConfig();
    }

    private void checkFilesLoaded() {
        numFilesLoaded ++;
        if (numFilesLoaded == files.length) {
            calculate();
        }
    }

    private int getWeekOfYear(OffsetDateTime dateTime) {
        return dateTime.get(wf.weekOfYear());
    }

    private void calculate() {
        fileService.shutdown();
        weekPeriodCoordinates = new ArrayList<>();
        points.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));

//        int woy = getWeekOfYear(points.get(0).getDate());
//        WeekPeriodCoordinates currentWeekPeriod = new WeekPeriodCoordinates(points.get(0));
//        for (int i = 1; i < points.size(); i++) {
//            Point point = points.get(i);
//            int week = getWeekOfYear(point.getDate());
//            if (woy == week || week == 0 ) {
//                currentWeekPeriod.addPoint(point);
//            }
//            else {
//                weekPeriodCoordinates.add(currentWeekPeriod);
//                if (week == 1) {
//                    woy = 1;
//                }
//                else if (week > 52){
//                    System.err.println("What");
//                }
//                else {
//                    woy++;
//                }
//                currentWeekPeriod = new WeekPeriodCoordinates(point);
//            }
//        }
        WeekPeriodCoordinates all = new WeekPeriodCoordinates(points);
        weekPeriodCoordinates.add(all);

        System.out.println("Finished dividing into weeks");

//        runAll();
        runBitMap();
    }

    private void runBitMap(){
        if (config == null|| weekPeriodCoordinates == null) {
            throw new RuntimeException("No config");
        }
        try {
            Builder bitMapBuilder = new BitMapBuilder(config, weekPeriodCoordinates);
            bitMapBuilder.run();
            bitMapBuilder.exportToJSON();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void runAll() {
        QuadTreeBuilder quadTreeBuilder = new QuadTreeBuilder(new ArrayList<>(points));
        QuadTree quadTree = quadTreeBuilder.run();
        outputAll(quadTree);
    }

    private WeekPeriodCoordinates output(WeekPeriodCoordinates previousPeriod, WeekPeriodCoordinates currentPeriod, QuadTree quadTree) {
        if (previousPeriod != null) {
            quadTree.setStartDate(previousPeriod.getBeginningOfWeek());
        }
        quadTree.setEndDate(currentPeriod.getBeginningOfWeek());
        System.out.println("before json");
        String json = gson.toJson(quadTree, QuadTree.class);
        System.out.println("After json");
        try {
            FileWriter fileWriter = new FileWriter(currentPeriod.getBeginningOfWeek().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + currentPeriod.getEndOfWeek().format(DateTimeFormatter.BASIC_ISO_DATE) + ".json");
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
            System.out.println("file done");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentPeriod;
    }

    private void outputAll(QuadTree quadTree) {
        try {
            System.out.println("full json file");
            String json = gson.toJson(quadTree, QuadTree.class);
            System.out.println("json done");
            FileWriter fileWriter = new FileWriter("AllData.json");
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();

            System.out.println("file done");
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void postKdFinished(){
        searchesComplete++;
        System.out.println("finished " + searchesComplete);
        if (searchesComplete == weekPeriodCoordinates.size()) {
            QuadTreeBuilderService.shutdown();
            System.out.println("done");
        }
    }

    private class QuadTreeBuilderThread implements Runnable {

        WeekPeriodCoordinates periodCoordinates;

        QuadTreeBuilderThread(WeekPeriodCoordinates period) {
            this.periodCoordinates = period;
        }

        @Override
        public void run() {
            System.out.println("Start thread");
            QuadTreeBuilder kdSearch = new QuadTreeBuilder(periodCoordinates.getPoints());
            QuadTree quadTree = kdSearch.run();
            output(null, periodCoordinates, quadTree);
            postKdFinished();
        }
    }

    public void start() {
        System.out.println("start loading files");
        for (String file: files) {
            Loader loader = new LoaderImpl(points, callback);
            loader.setup(file);
            fileService.submit(loader);
        }
    }
}
