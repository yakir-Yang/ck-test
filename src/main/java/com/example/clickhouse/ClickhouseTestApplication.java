package com.example.clickhouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ClickhouseTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClickhouseTestApplication.class, args);
    }
}
