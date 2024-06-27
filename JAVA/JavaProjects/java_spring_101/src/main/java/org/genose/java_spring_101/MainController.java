package org.genose.java_spring_101;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController

public class MainController implements ErrorController {


    @GetMapping("/greetings")
    @ResponseBody
    public String index() {
        return "Greetings from <a href='https://spring.io/projects/spring-boot'>Spring Boot!</a>";
    }

    @RequestMapping(value = "/error")
    @ResponseBody
    public ResponseEntity<String> errorPage() {

        return ResponseEntity.ok("Doctor in Error Greetings from <a href='https://spring.io/projects/spring-boot'>Spring Boot!</a>");
    }


}