package loader;

import controller.Callback;
import exceptions.ErrorCallback;
import exceptions.LoaderException;
import model.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;


public class LoaderImpl implements Loader {

    private String filepath;
    private File file;
    private String extension;
    private FileLoader loader;
    private List<Point> points;
    private ErrorCallback errorCallback;
    private Callback callback;

    public LoaderImpl(List<Point> points, Callback callback) {
        this.points = points;
        errorCallback = ((message, e) -> {
            System.err.println(message);
            e.printStackTrace();
        });
        this.callback = callback;
    }

    @Override
    public void setup(String filepath) {
        this.filepath = filepath;

        try {
            getFile();
        } catch (FileNotFoundException e) {
            errorCallback.callback("The file: specified could not be found", e);
        }

        try {
            determineFileType();
        } catch (LoaderException e) {
            errorCallback.callback("A critical error occurred", e);
        }
        loader.setErrorCallback(errorCallback);
    }

    private void getFile() throws FileNotFoundException {
        if (filepath == null) {
            throw new FileNotFoundException("File not specified");
        }
        file = new File(filepath);
        if (!file.exists()) {
            throw new FileNotFoundException("File could not be found");
        }
    }

    private void determineFileType() throws LoaderException {
        if(filepath != null && filepath.contains(".")) {
            extension = filepath.substring(filepath.lastIndexOf('.') + 1);
        }

        if (extension == null) {
            throw new LoaderException("file extension could not be determined");
        }

        switch (extension) {
            case "csv":
                loader = new CSVLoader(file, true);
                break;
            default:
                throw new LoaderException("File type not supported");
        }
    }

    @Override
    public void run() {
        System.out.println("reading from file");
        while (!loader.isFinished()) {
            Point point = loader.readFileLine();
            points.add(point);
        }
        System.out.println("done reading");
        callback.calculate();
    }
}
