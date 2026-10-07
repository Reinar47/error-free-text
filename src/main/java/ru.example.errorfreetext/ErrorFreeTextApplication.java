package ru.example.errorfreetext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.example.errorfreetext.dto.response.ErrorResponse;

@EnableScheduling
@SpringBootApplication
public class ErrorFreeTextApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErrorFreeTextApplication.class, args);}

}