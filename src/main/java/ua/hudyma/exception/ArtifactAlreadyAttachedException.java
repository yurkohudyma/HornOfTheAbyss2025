package ua.hudyma.exception;

public class ArtifactAlreadyAttachedException extends RuntimeException {
    public ArtifactAlreadyAttachedException(String message) {
        super(message);
    }
}
