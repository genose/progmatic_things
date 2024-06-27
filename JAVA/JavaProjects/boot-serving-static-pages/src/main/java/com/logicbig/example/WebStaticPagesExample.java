package com.logicbig.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
public class WebStaticPagesExample {

    public static void main (String[] args) {
        SpringApplication app =
                  new SpringApplication(WebStaticPagesExample.class);
        app.run(args);
    }
}