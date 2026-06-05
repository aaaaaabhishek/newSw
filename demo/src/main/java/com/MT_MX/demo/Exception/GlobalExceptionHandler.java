package com.MT_MX.demo.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MxParseException.class)
    public ResponseEntity<ApiError> handleMxParseException(
            MxParseException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("MX_PARSE_ERROR");
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
