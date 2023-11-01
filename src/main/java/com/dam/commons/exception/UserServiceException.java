package com.dam.commons.exception;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class UserServiceException extends RuntimeException{

    public UserServiceException(String message)
    {
        super(message);
    }

//        if (userEntity == null) {
//        throw new UserServiceException("User " + username + " not found");
//    }
}