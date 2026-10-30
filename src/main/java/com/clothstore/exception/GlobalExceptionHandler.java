package com.clothstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<APIErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().
                getFieldErrors().
                forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        APIErrorResponse apiErrorResponse = new APIErrorResponse();
        apiErrorResponse.setErrors(errors);
        apiErrorResponse.setMessage("Validation Failed");
        apiErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(apiErrorResponse);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleResourceNotFound(
            ResourceNotFoundException e) {

        APIErrorResponse response = new APIErrorResponse();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage(e.getMessage());
        response.setErrors(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<APIErrorResponse> handleDuplicateResource(
            DuplicateResourceException e) {

        APIErrorResponse response = new APIErrorResponse();
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setMessage(e.getMessage());
        response.setErrors(null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
