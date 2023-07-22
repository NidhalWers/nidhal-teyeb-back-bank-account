package exalt.kata.presentation.web.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionController
{
    @ExceptionHandler(value = ApplicationException.class)
     public ResponseEntity<ErrorResource> ApplicationExceptionHandler(ApplicationException exception)
    {
        return new ResponseEntity<>(new ErrorResource(exception.getErrorCode(), exception.getMessage()), ErrorResponseHelper.translate(exception.getErrorCode()));
    }
}
