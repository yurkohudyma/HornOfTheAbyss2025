package ua.hudyma.exception;

public class BuildingAlreadyExistsException extends RuntimeException {
    public BuildingAlreadyExistsException(String message) {
        super(message);
    }
}
