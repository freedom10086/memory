package com.tencent.memory.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class ErrorController extends AbstractErrorController {

    @Value("${server.error.path:/error}")
    private String errorPath;

    @Value("${server.error.include-stacktrace:never}")
    private String includeStacktrace;

    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);

        return new ResponseEntity<>(body, status);
    }

    protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
        if ("always".equalsIgnoreCase(includeStacktrace)) {
            return true;
        }

        if ("on_trace_para".equalsIgnoreCase(includeStacktrace)) {
            return getTraceParameter(request);
        }

        return false;
    }


    @Override
    public String getErrorPath() {
        return errorPath;
    }
}