package org.example.bakemaster_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BakemasterBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BakemasterBackendApplication.class, args);
    }

}
