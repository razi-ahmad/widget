package com.demo.controller;

import com.demo.exception.ApiBaseErrorCode;
import com.demo.exception.ApiException;
import com.demo.throttle.RateLimitControl;
import com.demo.util.IPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

public abstract class BaseController {

    @Value("${demo.throttling.timeWindow}")
    private int timeWindow;         // expressed in minute

    @Autowired
    private RateLimitControl rateLimitControl;

    protected void checkThrottle(String apiName, String key) {
        boolean allowed = rateLimitControl.isAllowed(apiName, timeWindow, TimeUnit.MINUTES, apiName, key);
        if (!allowed) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "throttle exceeded", ApiBaseErrorCode.TooManyRequests);
        }

    }

    protected void checkThrottle(HttpServletRequest request, String apiName) {
        checkThrottle(apiName, IPUtil.getRemoteIp(request));
    }
}