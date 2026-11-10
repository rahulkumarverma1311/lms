package com.micro.loans.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String resource,String fieldName,String value){
        super(String.format("%s not found with given input %s : %s",resource,fieldName,value));
    }
}
