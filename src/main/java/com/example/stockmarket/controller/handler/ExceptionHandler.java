package com.example.stockmarket.controller.handler;

import com.example.stockmarket.controller.response.ErrorResponse;
import com.example.stockmarket.exception.ExternalServiceException;
import com.example.stockmarket.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({UserException.class})
    public ResponseEntity<ErrorResponse> handleUserException(UserException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({ExternalServiceException.class})
    public ResponseEntity<ErrorResponse> handleExternalServiceException(ExternalServiceException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Внутренняя ошибка сервера, попробуйте позже");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
