package com.omo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OmoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmoBackendApplication.class, args);
	}

}
