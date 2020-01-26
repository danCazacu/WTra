package com.wtra.client.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wtra.client.service.DatabaseService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static com.wtra.client.controller.LoginController.TOKEN;
import static com.wtra.client.service.AuthenticationService.checkToken;
import static com.wtra.client.service.AuthenticationService.invalidateSession;

@Controller
public class HomeController {

    public static final String API_INSTANCE = "http://wtra-api.eu-west-1.elasticbeanstalk.com/";
    private static final HttpClient client = new DefaultHttpClient();


    @RequestMapping("/home")
    public String entry(HttpServletRequest request, Model model, @CookieValue(value = TOKEN, defaultValue = "") String token, HttpServletResponse httpServletResponse) {
        if (!token.isEmpty()) {
            return checkToken(token,httpServletResponse,"main");
        }
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap.size() < 2 || parameterMap.get("signInEmail") == null || parameterMap.get("signInPassword") == null)
                return "index";
            String mail = parameterMap.get("signInEmail")[0];
            String password = parameterMap.get("signInPassword")[0];

            HttpPost post = new HttpPost(API_INSTANCE + "/login");
            post.setHeader("Authorization", "Basic " + encode(mail, password));
            HttpResponse response = client.execute(post);

            String jsonResponseBody = IOUtils.toString(response.getEntity().getContent());
            Gson gson = new Gson();
            Map<String, Object> jsonResponse = gson.fromJson(jsonResponseBody, Map.class);
            Object session = jsonResponse.get("session");
            if (session != null && !session.toString().equals("")) {
                httpServletResponse.addCookie(new Cookie(TOKEN, session.toString()));
                return "main";
            }
            return "index";
        } catch (JsonSyntaxException notJson) {
            model.addAttribute("errorMessage", "Invalid credentials");
            invalidateSession(httpServletResponse);
            return "index";
        } catch (IOException e) {
            e.printStackTrace();
            return "index";
        }
    }

    private static String encode(String email, String password) {
        String toEncode = email + ":" + password;
        byte[] credEncoded = Base64.getEncoder().encode(toEncode.getBytes());
        return new String(credEncoded, StandardCharsets.UTF_8);
    }


}
