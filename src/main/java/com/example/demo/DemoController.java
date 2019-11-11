package com.example.demo;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping(value = "/hello")
    public String demo() {
        return "Hello, World!";
    }

    @GetMapping(value = "/hi")
    public String demo2() {
        return "Hi, World!";
    }
}