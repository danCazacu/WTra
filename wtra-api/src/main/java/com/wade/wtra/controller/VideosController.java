package com.wade.wtra.controller;

import com.google.gson.Gson;
import com.wade.wtra.service.LoginService;
import com.wade.wtra.service.VideoService;
import javafx.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import static com.wade.wtra.service.LoginService.getEmailByToken;
import static com.wade.wtra.service.VideoService.EXCEPTION_NOT_FOUND;
import static com.wade.wtra.service.VideoService.EXCEPTION_NOT_READY;

@RestController
public class VideosController {


    @PostMapping(value = "/videos", produces = "application/json")
    public ResponseEntity<String> uploadVideo(HttpServletRequest request, @RequestParam("video") MultipartFile file) {
        try {
            ResponseEntity<String> response = validateToken(request);
            if (response != null) return response;
            String filename = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(() -> new Exception("Invalid file name"));
            long id = VideoService.add(filename,getEmailByToken(request.getHeader("token")));
            return new ResponseEntity<>(computeResponse(id), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("File Upload Failed. "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> validateToken(HttpServletRequest request) {
        if (request.getHeader("token") == null) {
            return new ResponseEntity<>("Token not present", HttpStatus.FORBIDDEN);
        }
        String token = request.getHeader("token");
        try {
            if (!LoginService.validate(token)) {
                return new ResponseEntity<>("Invalid token. Login at: /login", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
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
        ResponseEntity<String> response = validateToken(request);
        if (response != null) return response;
        Long videoId = Long.parseLong(id);
        try {
            return new ResponseEntity<>(VideoService.getProcessed(videoId), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().equals(EXCEPTION_NOT_FOUND))
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            if(e.getMessage().equals(EXCEPTION_NOT_READY))
                return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/videos")
    public ResponseEntity<String> getVideoResult(HttpServletRequest request) {
        ResponseEntity<String> response = validateToken(request);
        if (response != null) return response;
        try {
            List<Pair<String, Object>> videosFromDatabase = VideoService.getVideos(getEmailByToken(request.getHeader("token")));
            Map<String,Object> userVideos = new HashMap<>();
            List<Object> videosList = new ArrayList<>();
            userVideos.put("videos",videosList);
            for (Pair<String, Object> stringObjectPair : videosFromDatabase) {
                HashMap<String,Object> video = new HashMap<>();
                video.put("name",stringObjectPair.getKey());
                video.put("id",stringObjectPair.getValue());
                video.put("resultAt","/videos/" +stringObjectPair.getValue());
                videosList.add(video);
            }
            return new ResponseEntity<>(new Gson().toJson(userVideos), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
