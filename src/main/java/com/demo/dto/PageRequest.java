package com.demo.dto;

import com.demo.exception.ApiBaseErrorCode;
import com.demo.exception.ApiException;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

public class PageRequest extends AbstractPageRequest {


    private PageRequest() {
        super(1, 10);
    }

    private PageRequest(int page, int pageSize) {
        super(page, pageSize);
    }


    public static PageRequest of(Integer pageNumber, Integer pageSize) {
        if (pageSize > 500) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Page size must not be greater than 500", ApiBaseErrorCode.InvalidIdParameter);
        }
        return new PageRequest(pageNumber, pageSize);
    }

    @Override
    public Sort getSort() {
        return null;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previous() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }
}
