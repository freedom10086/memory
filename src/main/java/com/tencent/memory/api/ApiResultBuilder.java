package com.tencent.memory.api;

import org.springframework.http.HttpStatus;

public class ApiResultBuilder<T> {

    private ApiResult<T> result;


    public  ApiResultBuilder() {
    }

    public ApiResultBuilder<T> success(T data) {
        checkNull();
        result.status = HttpStatus.OK.value();
        result.data = data;
        result.message = "success";
        return this;
    }

    public ApiResultBuilder<T> error(int status, String message) {
        checkNull();
        result.status = status;
        result.message = message;
        return this;
    }


    public ApiResult<T> build() {
        if (result == null) {
            throw new IllegalStateException("need set result");
        }
        return result;
    }

    private void checkNull() {
        if (result == null) {
            result = new ApiResult<>();
        }
    }
}
