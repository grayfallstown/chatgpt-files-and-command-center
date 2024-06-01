package net.grayfallstown.chatgptfileandcommandcenter.project;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class UnknownProjectExceptionHandler {

    @ExceptionHandler(UnknownProjectException.class)
    public ResponseEntity<Object> handleForbiddenException(UnknownProjectException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
    
}
