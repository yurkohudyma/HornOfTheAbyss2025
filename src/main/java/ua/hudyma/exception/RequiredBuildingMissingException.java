package ua.hudyma.exception;

public class RequiredBuildingMissingException extends RuntimeException{
    public RequiredBuildingMissingException(String message) {
        super(message);
    }
}
