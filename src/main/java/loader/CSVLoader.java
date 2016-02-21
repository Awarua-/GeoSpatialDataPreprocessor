package loader;

import exceptions.LoaderException;
import model.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CSVLoader extends FileLoader {

    private final String columnDelimiter = ",";
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private BufferedReader reader;
    private String tempLine;
    private int lineCounter;
    private Map<String, Integer> fieldMap;

    public CSVLoader(final File file, final boolean header) throws LoaderException {
        this.file = file;
        lineCounter = 0;
        fieldMap = new HashMap<>();

        if (this.file == null) {
            throw new LoaderException("No CSV file to read");
        }

        try {
            reader = Files.newBufferedReader(this.file.toPath(), charset);

        } catch (IOException e) {
            throw new LoaderException("Could not read from csv", e);
        }

        if (header) {
            readCSVHeaders();
        }

        readCSVLine();
    }

    private void readCSVHeaders() {
        Field[] fields = Point.class.getDeclaredFields();
        for (Field field : fields) {
            Annotation[]  annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof GeoZoneTables) {
                    fieldMap.put(((GeoZoneTables) annotation).columnName(), null);
                }
            }
        }
        try {
            String header = reader.readLine();
            String[] values = header.split(columnDelimiter);
            for (int i = 0; i < values.length; i++) {
                if (fieldMap.containsKey(values[i])) {
                    fieldMap.replace(values[i], i);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Point readFileLine() {
        Point point = null;
        do {
            lineCounter++;
            point = parseLine(tempLine);
            readCSVLine();
        } while (point == null && !finished);
        return point;
    }

    private void readCSVLine() {
        try {
            tempLine = reader.readLine();

            if (tempLine == null) {
                reader.close();
                finished = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            errorCallback.callback("Failed to read file", e);
            tempLine = null;
            finished = true;
        }

    }

    private Point parseLine(final String line) {
        String[] values = line.split(columnDelimiter);
        return new Point(values[fieldMap.get("id")], values[fieldMap.get("user_id")], values[fieldMap.get("latitude")], values[fieldMap.get("longitude")], values[fieldMap.get("created_at")]);
    }
}
