package com.szaleski.xmcy.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
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

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, "Missing 'month' parameter. Try adding it in format: '?month=yyyy-MM'", headers, status, request);
    }

}