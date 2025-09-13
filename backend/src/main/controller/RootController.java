package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to handle the root path and redirect to Swagger UI
 */
@Controller
public class RootController {

    /**
     * Redirects the root path to Swagger UI
     * @return redirect to Swagger UI
     */
    @GetMapping("/")
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }
}
