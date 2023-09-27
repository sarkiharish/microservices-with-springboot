package com.hari.microdemo.controller;

import com.hari.microdemo.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "Hello world!";
    }

    @GetMapping("/user")
    public User user() {
        User user = new User();
        user.setId("1");
        user.setName("Harish");
        user.setEmailId("harish@gmail.com");

        return  user;
    }

    @GetMapping("/{id}/{id2}")
    public String pathVariable(@PathVariable String id,
        @PathVariable("id2") String name) {
        return "The path variable is : " + id  + " : " + name;
    }

    @GetMapping("/requestParam")
    public String requestParam(@RequestParam String name,
                               @RequestParam(name = "email", defaultValue = "", required = false) String emailId) {
        return "Your name is " + name + " and EmailId is " + emailId;
    }

}
