/**
 * FILE: backend/src/main/java/com/interviewprep/InterviewPrepApplication.java
 *
 * WHAT IT DOES:
 *   Starts the Spring Boot backend, enables component scanning, and disables the unused default security user.
 *
 * ROLE IN THE PROJECT:
 *   This is the entry point used by Maven and the packaged backend jar.
 *
 * IF YOU REMOVE THIS FILE:
 *   The Spring Boot Maven plugin has no application class to launch.
 *
 * DEPENDS ON: Spring Boot and the application's no-login security configuration.
 * USED BY: Maven's spring-boot:run goal and the executable backend jar.
 *
 * KEY FUNCTIONS:
 *   - main(args): starts the Spring application.
 */
package com.interviewprep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class InterviewPrepApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewPrepApplication.class, args);
    }
}
