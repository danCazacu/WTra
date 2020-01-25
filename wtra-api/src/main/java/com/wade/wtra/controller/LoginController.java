package com.wade.wtra.controller;

import com.google.gson.Gson;
import com.wade.wtra.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    private static String AUTHORIZATION = "Authorization";
    private static String BASIC = "Basic";
    public static String INVALID_CREDENTIALS = "Invalid credentials. Try again.";

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<String> login(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(AUTHORIZATION);
            if (authHeader == null || !authHeader.contains(BASIC)) {
                throw new Exception(INVALID_CREDENTIALS);
            }
            String[] authHeaderValue = authHeader.split(" ");
            if (authHeader.length() < 2) {
                throw new Exception(INVALID_CREDENTIALS);
            }
            String base64Credentials = authHeaderValue[1];
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            return computeSuccessResponse(LoginService.getSessionId(values[0], values[1]));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage() != null ? e.getMessage() : "Something went wrong...", HttpStatus.BAD_REQUEST);
        }
    }

    public static ResponseEntity<String> computeSuccessResponse(String session) {
        Map<String, Object> json = new HashMap<>();
        json.put("success", "true");
        json.put("session", session);
        return new ResponseEntity<>(new Gson().toJson(json), HttpStatus.OK);

    }
}
