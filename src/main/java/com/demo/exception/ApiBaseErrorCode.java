package com.demo.exception;

public enum ApiBaseErrorCode {
    InvalidIdParameter,
    BadRequest,
    UnAuthorized,
    Forbidden,
    NotFound,
    TooManyRequests,
    InternalServerError,
    MethodNotAllowed,
    UnsupportedMediaType,
    UnknownError;

    ApiBaseErrorCode() {
    }

    public String getId() {
        return this.name();
    }
}