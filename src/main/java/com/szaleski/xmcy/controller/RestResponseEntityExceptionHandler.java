package com.szaleski.xmcy.controller;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.exceptions.UnknownCryptoSymbolException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UnknownCryptoSymbolException.class, CryptoDataNotAvailableException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleExceptionForTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String body = String.format("Invalid parameter '%s' value. [%s]", ex.getName(), ex.getRootCause().getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {

            String paramName = extractParamName(violation);
            sb.append(String.format("Invalid parameter '%s' value '%s'. %s.\n",
                                    paramName,
                                    violation.getInvalidValue(),
                                    violation.getMessage()));
        }
        return handleExceptionInternal(ex, sb.toString(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), headers, status, request);
    }

    private String extractParamName(ConstraintViolation<?> violation) {
        String[] pathParts = violation.getPropertyPath().toString().split("\\.");
        return pathParts[pathParts.length - 1];
    }

}