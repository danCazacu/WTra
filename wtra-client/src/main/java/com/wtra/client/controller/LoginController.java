package com.wtra.client.controller;

import com.wtra.client.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.wtra.client.service.AuthenticationService.invalidateSession;

@Controller
public class LoginController {

    public static final String TOKEN = "token";

    @RequestMapping({"/", "/login"})
    public String entry(Model model, @CookieValue(value = TOKEN, defaultValue = "") String token, HttpServletResponse httpServletResponse) {
//        if (!token.isEmpty()) {
//            return checkToken(token, httpServletResponse);
//        }
        invalidateSession(httpServletResponse);
        return "index";
    }

    @RequestMapping("/logout")
    public String logout(Model model, @CookieValue(value = TOKEN, defaultValue = "") String token, HttpServletResponse httpServletResponse) {
        invalidateSession(httpServletResponse);
        return "index";
    }

    @RequestMapping("/register")
    public String register(HttpServletRequest request, Model model) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.size() < 3 || parameterMap.get("registerName") == null || parameterMap.get("registerEmail") == null && parameterMap.get("registerPassword") == null)
            return "index";
        String name = parameterMap.get("registerName")[0];
        String mail = parameterMap.get("registerEmail")[0];
        String password = parameterMap.get("registerPassword")[0];
        if(mail.isEmpty() || name.isEmpty() || password.isEmpty())
            return "index";
        try {
            if(AuthenticationService.isAlreadyRegistered(mail)){
                model.addAttribute("errorMessage","This email is already registered");
                return "index";
            }
            if(AuthenticationService.register(name,mail,password)){
                model.addAttribute("errorMessage","Succesfully registered");
                return "index";
            }else{
                model.addAttribute("errorMessage","Something went wrong.");
                return "index";
            }
        } catch (Exception e) {
            return "index";
        }
    }

}
