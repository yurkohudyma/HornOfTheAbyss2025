package ua.hudyma.exception;

public class NoAvailableCreaturesForHireException extends RuntimeException{
    public NoAvailableCreaturesForHireException(String message) {
        super(message);
    }
}
