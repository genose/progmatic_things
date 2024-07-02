package org.genose.java_spring_101;

import org.genose.java_spring_101.Person;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
public class MainController implements ErrorController {


    @GetMapping("/greetings")

    public String index() {
        return "Greetings from <a href='https://spring.io/projects/spring-boot'>Spring Boot!</a>";
    }

    @GetMapping("/persons")
    public ResponseEntity<Person> personSample() {
        Person person = new Person("John Doe", 30);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/example")
    public ResponseEntity<String> example() {
        return ResponseEntity.ok("This is an example response");
    }

    @GetMapping("/with-headers")
    public ResponseEntity<String> withHeaders() {
        return ResponseEntity.ok()
                .header("Custom-Header", "value")
                .body("Response with custom header");
    }


    @GetMapping("/no-content")
    public ResponseEntity<?> noContent() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movie")
    @ResponseBody
    public String showTitle(@RequestParam String title) {

        return "The movie's title is " + title;

    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resource not found");
    }


}