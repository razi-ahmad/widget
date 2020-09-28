package com.demo.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class ApiErrorResponse {
    private String code;
    private String message;

    public ApiErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public ApiErrorResponse(String message) {
        this.message = message;
    }

    public ApiErrorResponse() {
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }
}
