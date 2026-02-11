package com.hdc.hdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HdcApplication {

	public static void main(String[] args) {
		SpringApplication.run(HdcApplication.class, args);
	}
}