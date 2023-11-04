package com.dam.commons;

import org.springframework.http.HttpStatus;

/**
 * Created by mj.rahmati on 11/27/2021
 */
public interface ExceptionType {
    HttpStatus getHttpStatus();
    int getErrorCode();
    String getMessageKey();
}
