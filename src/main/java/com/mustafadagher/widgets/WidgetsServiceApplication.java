package com.mustafadagher.widgets;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WidgetsServiceApplication {

    public static void main(String[] args) {
        new SpringApplication(WidgetsServiceApplication.class).run(args);
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}
