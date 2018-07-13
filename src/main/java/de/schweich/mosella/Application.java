package de.schweich.mosella;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        
        System.setProperty("java.net.useSystemProxies", "true");
        SpringApplication.run(Application.class, args);
    }
}