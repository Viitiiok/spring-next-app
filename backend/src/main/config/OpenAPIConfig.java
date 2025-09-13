package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Contact contact = new Contact();
        contact.setEmail("your.email@example.com");
        contact.setName("Your Name");
        contact.setUrl("https://your-website.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Spring Boot API")
                .version("1.0.0")
                .contact(contact)
                .description("This API exposes endpoints for the Spring Boot application.")
                .license(mitLicense);

        return new OpenAPI().info(info);
    }
}
