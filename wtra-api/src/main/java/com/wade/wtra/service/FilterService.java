package com.wade.wtra.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

public class FilterService<T> {

    private List<String> supportedMediaTypes = Collections.singletonList(MediaType.APPLICATION_JSON_VALUE);

    public ResponseEntity<T> filter(HttpServletRequest request) {
        if(!supportedMediaTypes.contains(request.getHeader("Accept"))) {
            return new ResponseEntity<T>(HttpStatus.NOT_ACCEPTABLE);
        }
        return null;
    }
}
