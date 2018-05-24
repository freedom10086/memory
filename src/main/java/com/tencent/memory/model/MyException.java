package com.tencent.memory.model;

import org.springframework.http.HttpStatus;

public class MyException extends RuntimeException {
    public HttpStatus status;
    public String message;

    public MyException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public MyException(Exception e) {
        super(e.getMessage());
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = e.getMessage();
    }
}