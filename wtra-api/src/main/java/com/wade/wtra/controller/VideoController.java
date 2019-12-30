package com.wade.wtra.controller;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController("/video")
public class VideoController {



    @PostMapping(value = "/upload", produces = "application/json")
    public ResponseEntity<String> uploadVideo(HttpServletRequest request, @RequestParam("video") MultipartFile file) {
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        try {
            FileOutputStream fileWriter = new FileOutputStream(Optional.ofNullable(file.getOriginalFilename()).orElse("video.mov"));
            fileWriter.write(file.getBytes());
            response = new ResponseEntity<>(computeResponse(), HttpStatus.CREATED);
        } catch (IOException e) {
            new ResponseEntity<>("File Upload Failed", HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return response;
    }

    private String computeResponse(){
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("success",true);
        jsonBody.put("resultAt","/video/result/{id}");
        jsonBody.put("id","{id}");
        Gson gson = new Gson();
        return gson.toJson(jsonBody);
    }
}
