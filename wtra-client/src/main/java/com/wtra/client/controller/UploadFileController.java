package com.wtra.client.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static com.wtra.client.controller.HomeController.API_INSTANCE;
import static com.wtra.client.controller.LoginController.TOKEN;
import static com.wtra.client.service.AuthenticationService.checkToken;

@Controller
public class UploadFileController {

    private static final HttpClient client = new DefaultHttpClient();

    @PostMapping("uploadFile")
    public String uploadFile(Model model, @CookieValue(value = TOKEN, defaultValue = "") String token, HttpServletResponse httpServletResponse, @RequestParam("video") MultipartFile file) {
        if (!token.isEmpty()) {
            String retur = checkToken(token, httpServletResponse, "UploadFileController");
            if (!retur.equals("UploadFileController"))
                return retur;
        }
        String bodyResponse = "";
        try {
//            String filename = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(() -> new Exception("Invalid file name"));
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("video", file.getResource());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("token", token);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:5000" + "/videos", requestEntity, String.class);
            bodyResponse = responseEntity.getBody();
            if(responseEntity.getStatusCode().value() == 201) {
                model.addAttribute("uploadStatus", "File uploaded succesfully");
            }
            return "main";
        } catch (Exception e) {
            model.addAttribute("uploadStatus", "Something went wrong. Please try again later."+bodyResponse);
            return "main";
        }
    }
}
