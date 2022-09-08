package com.lkws.ttt.controller;

import com.lkws.ttt.datatransferobjects.ErrorDTO;
import com.lkws.ttt.model.ShareNotFoundException;
import com.lkws.ttt.model.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

/**
 * Catch general exceptions and convert them to a readable JSON response.
 * Specific errors should be put in the affected controller.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleValidationException(HttpServletRequest request, ValidationException ex) {
        log.error("ValidationException in" + request.getRequestURI() + ": " + ex.getMessage());

        return ResponseEntity.badRequest()
                .body(new ErrorDTO("Validation Exception", ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleUserNotFoundException(HttpServletRequest request, UserNotFoundException ex) {
        log.error("UserNotFoundException in" + request.getRequestURI() + ": " + ex.getMessage());

        return ResponseEntity.internalServerError()
                .body(new ErrorDTO("User not found", ex.getMessage()));
    }

    @ExceptionHandler(ShareNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleShareNotFoundException(HttpServletRequest request, ShareNotFoundException ex) {
        log.error("ShareNotFoundException in" + request.getRequestURI() + ": " + ex.getMessage());

        return ResponseEntity.internalServerError()
                .body(new ErrorDTO("Share not found", ex.getMessage()));
    }

}
