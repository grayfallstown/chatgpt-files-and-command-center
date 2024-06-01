package net.grayfallstown.chatgptfileandcommandcenter.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import net.grayfallstown.chatgptfileandcommandcenter.file.FileNotFoundException;
import net.grayfallstown.chatgptfileandcommandcenter.file.FileOperationException;
import net.grayfallstown.chatgptfileandcommandcenter.history.GitOperationException;
import net.grayfallstown.chatgptfileandcommandcenter.project.UnknownProjectException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UnknownProjectException.class)
    public ResponseEntity<Object> handleUnknownProjectException(UnknownProjectException ex, WebRequest request) {
        logger.error("Unknown project exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<Object> handleFileOperationException(FileOperationException ex, WebRequest request) {
        logger.error("File operation exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitOperationException.class)
    public ResponseEntity<Object> handleGitOperationException(GitOperationException ex, WebRequest request) {
        logger.error("Git operation exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException ex, WebRequest request) {
        logger.error("File not found exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        logger.error("Validation exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Generic exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}