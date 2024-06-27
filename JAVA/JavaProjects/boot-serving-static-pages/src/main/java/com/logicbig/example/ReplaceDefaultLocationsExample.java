package com.logicbig.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@EnableAutoConfiguration
public class ReplaceDefaultLocationsExample {

    public static void main (String[] args) {

        SpringApplication app =
                  new SpringApplication(ReplaceDefaultLocationsExample.class);

        Properties properties = new Properties();
        properties.setProperty("spring.resources.static-locations",
                               "classpath:/newLocation1/, classpath:/newLocation2/");
        app.setDefaultProperties(properties);
        app.run(args);
    }
}