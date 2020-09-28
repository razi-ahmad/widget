package com.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class BaseExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BaseExceptionHandler.class);
    private static final Map<Class<? extends Exception>, HttpStatus> SPRING_BUILTIN_EXCEPTION_MAPPINGS;
    private static final Map<Class<? extends Exception>, HttpStatus> KNOWN_EXCEPTION_MAPPINGS;

    public BaseExceptionHandler() {
    }

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ApiErrorResponse> handleException(ApiException e) {
        LOG.debug("{}: {}", e.getHttpStatus(), e.getMessage());
        return new ResponseEntity<>(e.getApiErrorResponse(), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleException(MethodArgumentNotValidException e) {
        LOG.error("unhandled api exception", e);
        HttpStatus code = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        ApiErrorResponse res = new ApiErrorResponse(errors.toString(), ApiBaseErrorCode.BadRequest.getId());
        return new ResponseEntity<>(res, code);
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        HttpStatus code;
        ApiException apiExp;
        if (SPRING_BUILTIN_EXCEPTION_MAPPINGS.containsKey(e.getClass())) {
            code = SPRING_BUILTIN_EXCEPTION_MAPPINGS.get(e.getClass());
            apiExp = new ApiException(code);
            LOG.info("exception in spring built in {}  {}", e.getClass().getName(), e.getMessage());

            return e instanceof HttpMediaTypeNotAcceptableException ? new ResponseEntity<>(null, code) : new ResponseEntity<>(apiExp.getApiErrorResponse(), code);
        } else if (KNOWN_EXCEPTION_MAPPINGS.containsKey(e.getClass())) {
            code = KNOWN_EXCEPTION_MAPPINGS.get(e.getClass());
            apiExp = new ApiException(code);
            LOG.info("exception in known mapping {}  {} ", e.getClass().getName(), e.getMessage());
            return new ResponseEntity<>(apiExp.getApiErrorResponse(), code);
        } else {
            LOG.error("unhandled api exception", e);
            code = HttpStatus.INTERNAL_SERVER_ERROR;
            ApiErrorResponse res = new ApiErrorResponse("Internal server error, the event will be logged and analysed.", ApiBaseErrorCode.InternalServerError.getId());
            return new ResponseEntity<>(res, code);
        }
    }

    static {
        SPRING_BUILTIN_EXCEPTION_MAPPINGS = Map.ofEntries(Map.entry(HttpRequestMethodNotSupportedException.class, HttpStatus.METHOD_NOT_ALLOWED), Map.entry(HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE), Map.entry(HttpMediaTypeNotAcceptableException.class, HttpStatus.NOT_ACCEPTABLE), Map.entry(MissingPathVariableException.class, HttpStatus.BAD_REQUEST), Map.entry(MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST), Map.entry(ServletRequestBindingException.class, HttpStatus.BAD_REQUEST), Map.entry(ConversionNotSupportedException.class, HttpStatus.BAD_REQUEST), Map.entry(TypeMismatchException.class, HttpStatus.BAD_REQUEST), Map.entry(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST), Map.entry(HttpMessageNotWritableException.class, HttpStatus.BAD_REQUEST), Map.entry(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST), Map.entry(MissingServletRequestPartException.class, HttpStatus.BAD_REQUEST), Map.entry(BindException.class, HttpStatus.BAD_REQUEST), Map.entry(NoHandlerFoundException.class, HttpStatus.BAD_REQUEST), Map.entry(AsyncRequestTimeoutException.class, HttpStatus.BAD_REQUEST));
        KNOWN_EXCEPTION_MAPPINGS = Map.ofEntries(Map.entry(IllegalArgumentException.class, HttpStatus.BAD_REQUEST));
    }
}