package apollong.dspace;

public class GpsNotReadyException extends Exception {
    public GpsNotReadyException() {
    }
    public GpsNotReadyException(String message) {
        super(message);
    }
}