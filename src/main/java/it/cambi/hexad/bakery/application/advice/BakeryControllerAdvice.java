package it.cambi.hexad.bakery.application.advice;

import it.cambi.hexad.bakery.exception.BakeryException;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BakeryControllerAdvice {

  @ExceptionHandler({BakeryException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public @ResponseBody ResponseEntity<ErrorResponse> badRequestResponse(Exception ex) {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .error(httpStatus.getReasonPhrase())
            .message(ex.getMessage())
            .status(httpStatus.value())
            .timestamp(new Date())
            .build();

    return ResponseEntity.status(httpStatus).body(errorResponse);
  }
}
