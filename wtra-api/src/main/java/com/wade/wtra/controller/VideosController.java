package com.wade.wtra.controller;

import com.google.gson.Gson;
import com.wade.wtra.service.VideosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class VideosController {

    private VideosService service = new VideosService();

    @PostMapping(value = "/videos", produces = "application/json")
    public ResponseEntity<String> uploadVideo(HttpServletRequest request, @RequestParam("video") MultipartFile file) {
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        try {
//            FileOutputStream fileWriter = new FileOutputStream(Optional.ofNullable(file.getOriginalFilename()).orElse("video.mov")); //THROWS EXCEPTION WHEN THERE IS NOT FILE BECAUSE IT CANNOT CREATE VIDEO.MOV out of the box
            //fileWriter.write(file.getBytes()); disabled for now, should write to S3
            response = new ResponseEntity<>(computeResponse(), HttpStatus.CREATED);
        } catch (Exception e) {
            new ResponseEntity<>("File Upload Failed", HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        return response;
    }

    private String computeResponse() {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("success", true);
        jsonBody.put("resultAt", "/videos/{id}");
        jsonBody.put("id", "{id}");
        Gson gson = new Gson();
        return gson.toJson(jsonBody);
    }

    @GetMapping(value = "/videos/{id}")
    public ResponseEntity<String> getVideoResult(HttpServletRequest request, @PathVariable("id") String id){
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        return response;
    }
}
