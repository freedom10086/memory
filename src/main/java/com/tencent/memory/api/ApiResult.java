package com.tencent.memory.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.memory.model.MyException;
import org.springframework.http.HttpStatus;

public class ApiResult<T> {
    public int status;
    public String message;
    public T data;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
