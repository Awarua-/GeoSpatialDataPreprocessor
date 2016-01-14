package exceptions;

@FunctionalInterface
public interface ErrorCallback {
    void callback(String message, Throwable e);
}
