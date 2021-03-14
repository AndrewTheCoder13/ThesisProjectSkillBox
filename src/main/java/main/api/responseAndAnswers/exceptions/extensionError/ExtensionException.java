package main.api.responseAndAnswers.exceptions.extensionError;

import main.api.responseAndAnswers.image.ImageErrors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.InvalidPropertiesFormatException;

@ControllerAdvice
public class ExtensionException extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidPropertiesFormatException.class)
    protected ResponseEntity<ExtensionError> handlerMaxLimitException(){
        ExtensionError extensionError = new ExtensionError();
        extensionError.setResult(false);
        ImageErrors errors = new ImageErrors();
        errors.setImage("Неверное форматирование файла");
        extensionError.setErrors(errors);
        return ResponseEntity.badRequest().body(extensionError);
    }
}
