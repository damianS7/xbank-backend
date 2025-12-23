package com.damian.xBank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class xBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(xBankApplication.class, args);
    }

}
