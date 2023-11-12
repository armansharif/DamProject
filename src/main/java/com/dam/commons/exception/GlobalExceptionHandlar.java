package com.dam.commons.exception;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Locale;

@ControllerAdvice

public class GlobalExceptionHandlar {
    MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandlar(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> generateException(ResponseStatusException re) {
        ErrorDTO dto = new ErrorDTO();
        dto.setTimestamp(new Date().toString());
        dto.setStatus(String.valueOf(re.getStatus().value()));
        dto.setMessage(messageSource.getMessage(re.getReason(), null, Locale.getDefault()));
        return new ResponseEntity<ErrorDTO>(dto, re.getStatus());
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> generateException(RuntimeException re) {
        ErrorDTO dto = new ErrorDTO();
        dto.setTimestamp(new Date().toString());
        dto.setStatus("500");
        dto.setMessage(re.getMessage());

        return new ResponseEntity<ErrorDTO>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}