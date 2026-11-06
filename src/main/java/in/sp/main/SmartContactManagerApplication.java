package in.sp.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartContactManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartContactManagerApplication.class, args);

        System.out.println("Spring Boot Smart Contact Manager Project Started Successfully");
    }
}