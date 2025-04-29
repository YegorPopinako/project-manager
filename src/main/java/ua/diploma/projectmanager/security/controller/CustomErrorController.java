package ua.diploma.projectmanager.security.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/unauthorized")
    public String unauthorized() {
        return "401";
    }
}
