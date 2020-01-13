package com.wade.wtra.controller;

import com.wade.wtra.service.SingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SignsController {

    private SingsService service = new SingsService();

    @GetMapping(value = "/signs")
    public ResponseEntity<String> getSigns(HttpServletRequest request){
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        return response;
    }

    @GetMapping(value = "/signs/{name}")
    public ResponseEntity<String> getSignByName(HttpServletRequest request){
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);

        return response;
    }
}
