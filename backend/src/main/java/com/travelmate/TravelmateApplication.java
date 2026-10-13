package com.travelmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.travelmate", "app"})
@EntityScan(basePackages = {"com.travelmate.model", "app.entity"})
@EnableJpaRepositories(basePackages = {"com.travelmate.repository", "app.repository"})
public class TravelmateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelmateApplication.class, args);
    }
}
