package controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import loader.Loader;
import loader.LoaderImpl;
import model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
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
    private KDSearch kdSearch;
    private ExecutorService fileService;
    private ExecutorService kdSearchService;
    private WeekFields wf = WeekFields.of(Locale.ENGLISH);
    private List<WeekPeriodCoordinates> weekPeriodCoordinates;
    private int searchesComplete;
    private Gson gson;

    public Controller(String[] files) {
        this.files = files;
        numFilesLoaded = 0;
        searchesComplete = 0;
        points = Collections.synchronizedList(new ArrayList<>());
        callback = this::checkFilesLoaded;
        fileService = Executors.newFixedThreadPool(files.length);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(KDTree.class, new KDTreeSerializer());
        gson = gsonBuilder.create();
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

        int woy = getWeekOfYear(points.get(0).getDate());
        WeekPeriodCoordinates currentWeekPeriod = new WeekPeriodCoordinates(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            Point point = points.get(i);
            int week = getWeekOfYear(point.getDate());
            if (woy == week || week == 0 ) {
                currentWeekPeriod.addPoint(point);
            }
            else {
                weekPeriodCoordinates.add(currentWeekPeriod);
                if (week == 1) {
                    woy = 1;
                }
                else if (week > 52){
                    System.err.println("What");
                }
                else {
                    woy++;
                }
                currentWeekPeriod = new WeekPeriodCoordinates(point);
            }
        }

//        kdSearchService = Executors.newFixedThreadPool(weekPeriodCoordinates.size());

        WeekPeriodCoordinates previousPeriod = null;
        for (WeekPeriodCoordinates period : weekPeriodCoordinates) {
//            KdSearchThread thread = new KdSearchThread(period);
//            kdSearchService.submit(thread);
            KDSearch kdSearch = new KDSearch(period.getPoints());
            KDTree kdTree = kdSearch.run();
            if (previousPeriod != null) {
                kdTree.setStartDate(previousPeriod.getBeginningOfWeek());
            }
            kdTree.setEndDate(period.getBeginningOfWeek());
            System.out.println("before json");
            String json = gson.toJson(kdTree, KDTree.class);
            System.out.println("After json");
            try {
                FileWriter fileWriter = new FileWriter(period.getBeginningOfWeek().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + period.getEndOfWeek().format(DateTimeFormatter.BASIC_ISO_DATE) + ".json");
                fileWriter.write(json);
                fileWriter.flush();
                fileWriter.close();
                System.out.println("file done");
            } catch (IOException e) {
                e.printStackTrace();
            }
            previousPeriod = period;
        }
    }

    private void postKdFinished(){
        searchesComplete++;
        System.out.println("finished " + searchesComplete);
        if (searchesComplete == weekPeriodCoordinates.size()) {
            kdSearchService.shutdown();
            System.out.println("done");
        }
    }

    private class KdSearchThread implements Runnable {

        WeekPeriodCoordinates periodCoordinates;

        public KdSearchThread(WeekPeriodCoordinates period) {
            this.periodCoordinates = period;
        }

        @Override
        public void run() {
            KDSearch kdSearch = new KDSearch(periodCoordinates.getPoints());
            KDTree kdTree = kdSearch.run();
            postKdFinished();
        }
    }

    public void start() {
        for (String file: files) {
            Loader loader = new LoaderImpl(points, callback);
            loader.setup(file);
            fileService.submit(loader);
        }
    }
}
