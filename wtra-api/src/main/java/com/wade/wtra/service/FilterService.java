package com.wade.wtra.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class FilterService<T> {

    private List<String> supportedMediaTypes = Arrays.asList(MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE);

    public ResponseEntity<T> filter(HttpServletRequest request) {
        if(!supportedMediaTypes.contains(request.getHeader("Accept"))) {
            return new ResponseEntity<T>(HttpStatus.NOT_ACCEPTABLE);
        }
        return null;
    }
}
