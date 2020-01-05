package com.wtra.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String entry(@RequestParam(value = "signInEmail", required = false) String signInEmail, @RequestParam(value = "signInPassword", required = false) String signInPassword, Model model) {
        return "main.html";
    }
}
