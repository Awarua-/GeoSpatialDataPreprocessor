package exceptions;


/**
 * Created by dionw on 9/12/15.
 */
public class LoaderException extends Exception {

    public LoaderException(String message) {
        super(message);
    }

    public LoaderException(Throwable throwable) {
        super(throwable);
    }

    public LoaderException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
