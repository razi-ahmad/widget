package com.demo.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private HttpStatus httpStatus;
    private ApiErrorResponse apiErrorResponse;

    public ApiException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.apiErrorResponse = new ApiErrorResponse(httpStatus.getReasonPhrase(), this.translateHttpStatus(httpStatus).name());
    }

    public ApiException(HttpStatus httpStatus, String message, String code) {
        this.httpStatus = httpStatus;
        this.apiErrorResponse = new ApiErrorResponse(message, code);
    }

    public ApiException(HttpStatus httpStatus, String message, ApiBaseErrorCode code) {
        this.httpStatus = httpStatus;
        this.apiErrorResponse = new ApiErrorResponse(message, code.name());
    }

    public ApiException(HttpStatus httpStatus, String message, Enum e) {
        this.httpStatus = httpStatus;
        this.apiErrorResponse = new ApiErrorResponse(message, e.name());
    }

    public ApiException(HttpStatus httpStatus, ApiErrorResponse apiErrorResponse) {
        this.httpStatus = httpStatus;
        this.apiErrorResponse = apiErrorResponse;
    }

    public String getMessage() {
        return this.apiErrorResponse.getMessage();
    }

    public String getCode() {
        return this.apiErrorResponse.getCode();
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public ApiErrorResponse getApiErrorResponse() {
        return this.apiErrorResponse;
    }

    private ApiBaseErrorCode translateHttpStatus(HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.BAD_REQUEST) {
            return ApiBaseErrorCode.BadRequest;
        } else if (httpStatus == HttpStatus.UNAUTHORIZED) {
            return ApiBaseErrorCode.UnAuthorized;
        } else if (httpStatus == HttpStatus.FORBIDDEN) {
            return ApiBaseErrorCode.Forbidden;
        } else if (httpStatus == HttpStatus.NOT_FOUND) {
            return ApiBaseErrorCode.NotFound;
        } else if (httpStatus == HttpStatus.TOO_MANY_REQUESTS) {
            return ApiBaseErrorCode.TooManyRequests;
        } else if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            return ApiBaseErrorCode.InternalServerError;
        } else if (httpStatus == HttpStatus.METHOD_NOT_ALLOWED) {
            return ApiBaseErrorCode.MethodNotAllowed;
        } else {
            return httpStatus == HttpStatus.UNSUPPORTED_MEDIA_TYPE ? ApiBaseErrorCode.UnsupportedMediaType : ApiBaseErrorCode.UnknownError;
        }
    }
}