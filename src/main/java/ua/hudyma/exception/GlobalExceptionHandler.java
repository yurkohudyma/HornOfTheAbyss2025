package ua.hudyma.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ArtifactAlreadyAttachedException.class)
    public ResponseEntity<ErrorResponse> artifactAlreadyAttachedException(
            ArtifactAlreadyAttachedException ex) {
        var error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RequiredBuildingMissingException.class)
    public ResponseEntity<ErrorResponse> requiredBuildingMissingException(
            RequiredBuildingMissingException ex) {
        var error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientResourcesException.class)
    public ResponseEntity<ErrorResponse> InsufficientResourcesException(
            InsufficientResourcesException ex) {
        var error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }



    @ExceptionHandler(MinimalUnitOperationException.class)
    public ResponseEntity<ErrorResponse> MinimalUnitOperationException(
            MinimalUnitOperationException ex) {
        var error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EnumMappingErrorException.class)
    public ResponseEntity<ErrorResponse> EnumMappingErrorException(
            EnumMappingErrorException ex) {
        var error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ArmyFreeSlotOverflowException.class)
    public ResponseEntity<ErrorResponse> ArmyFreeSlotOverflowException(
            ArmyFreeSlotOverflowException ex) {
        var error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> EntityNotFoundException(
            EntityNotFoundException ex) {
        var error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DtoObligatoryFieldsAreMissingException.class)
    public ResponseEntity<ErrorResponse> DtoObligatoryFieldsAreMissingException(
            DtoObligatoryFieldsAreMissingException ex) {
        var error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> EntityAlreadyExistsException(
            EntityAlreadyExistsException ex) {
        var error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        var error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Holy crap! " + ex.getMessage(),
                now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public record ErrorResponse(int status, String message, LocalDateTime timestamp) {
    }
}
