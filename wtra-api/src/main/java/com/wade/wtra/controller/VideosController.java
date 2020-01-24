package com.wade.wtra.controller;

import com.google.gson.Gson;
import com.wade.wtra.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class VideosController {


    @PostMapping(value = "/videos", produces = "application/json")
    public ResponseEntity<String> uploadVideo(HttpServletRequest request, @RequestParam("video") MultipartFile file) {
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        try {
            String filename = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(() -> new Exception("Invalid file name"));
            long id = VideoService.add(filename);
            response = new ResponseEntity<>(computeResponse(id), HttpStatus.CREATED);
        } catch (Exception e) {
            new ResponseEntity<>("File Upload Failed", HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        return response;
    }

    private String computeResponse(long id) {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("success", true);
        jsonBody.put("resultAt", "/videos/" + id);
        jsonBody.put("id", id);
        Gson gson = new Gson();
        return gson.toJson(jsonBody);
    }

    @GetMapping(value = "/videos/{id}")
    public ResponseEntity<String> getVideoResult(HttpServletRequest request, @PathVariable("id") String id) {
        Long videoId = Long.parseLong(id);
        try {
            VideoService.getNameById(videoId);
        } catch (Exception e) {
            return new ResponseEntity<String>("No video with this ID exists", HttpStatus.NOT_FOUND);
        }
        if (!VideoService.isProcessed(videoId)) {
            return new ResponseEntity<String>("Processing not finished for this video", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<String>(VideoService.getProcessed(videoId).toString(),HttpStatus.OK);
    }
}
