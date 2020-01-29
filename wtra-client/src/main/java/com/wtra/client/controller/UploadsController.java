package com.wtra.client.controller;

import com.wtra.client.pojo.VideoPOJO;
import com.wtra.client.service.DatabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.wtra.client.controller.LoginController.TOKEN;
import static com.wtra.client.service.AuthenticationService.checkToken;
import static com.wtra.client.service.AuthenticationService.invalidateSession;

@Controller
public class UploadsController {

    @RequestMapping("/uploads")
    public String uploads(Model model, @CookieValue(value = TOKEN, defaultValue = "") String token, HttpServletResponse httpServletResponse){
        if (!token.isEmpty()) {
            String retur = checkToken(token, httpServletResponse,"uploads");
            if(!retur.equals("uploads"))
                return retur;
        }
        return enrichModel(model,token,httpServletResponse);

    }

    public static String enrichModel(Model model, String session, HttpServletResponse httpServletResponse) {
        String email;
        try {
            email = DatabaseService.getEmailByToken(session);
        } catch (Exception e) {
            invalidateSession(httpServletResponse);
            e.printStackTrace();
            return "index";
        }
        if (email == null) {
            invalidateSession(httpServletResponse);
            return "index";
        }
        try {
            List<VideoPOJO> videos = DatabaseService.getVideos(email);
//            for (Map.Entry<String, Object> stringObjectEntry : videos.entrySet()) {
//                stringObjectEntry.get
//            }
            model.addAttribute("videos",videos);
        } catch (Exception e) {
            invalidateSession(httpServletResponse);
            e.printStackTrace();
            return "index";
        }

        return "uploads";
    }
}
