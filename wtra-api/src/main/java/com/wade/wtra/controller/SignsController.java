package com.wade.wtra.controller;

import com.wade.wtra.service.FilterService;
import com.wade.wtra.service.StardogService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SignsController {

    @GetMapping(value = "/signs")
    public ResponseEntity<String> getSigns(HttpServletRequest request) {
        ResponseEntity<String> filtered = new FilterService<String>().filter(request);
        if (filtered != null)
            return filtered;

        try {
            HttpResponse response = StardogService.execute("select ?sub where { ?sub rdf:type WTra:Priority_Sign }");
            String body = IOUtils.toString(response.getEntity().getContent());
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/signs/{name}")
    public ResponseEntity<String> getSignByName(HttpServletRequest request,@PathVariable("name") String name) {
        ResponseEntity<String> filtered = new FilterService<String>().filter(request);
        if (filtered != null)
            return filtered;

        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        return response;
    }
}
