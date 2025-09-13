package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
@Tag(name = "Hello World", description = "A simple hello world controller")
public class HelloWorldController {

    @GetMapping
    @Operation(summary = "Get a hello message", 
               description = "Returns a simple hello world message")
    public String sayHello() {
        return "Hello, World!";
    }
}
