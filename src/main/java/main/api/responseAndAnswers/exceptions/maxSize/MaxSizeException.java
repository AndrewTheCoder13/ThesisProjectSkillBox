package main.api.responseAndAnswers.exceptions.maxSize;

import main.api.responseAndAnswers.image.ImageErrors;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MaxSizeException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SizeException.class)
    protected ResponseEntity<MaxSize> handlerMaxLimitException(){
        MaxSize size = new MaxSize();
        size.setResult(false);
        ImageErrors errors = new ImageErrors();
        errors.setImage("Размер файла превышает допустимый размер");
        size.setErrors(errors);
        return ResponseEntity.badRequest().body(size);
    }
}
