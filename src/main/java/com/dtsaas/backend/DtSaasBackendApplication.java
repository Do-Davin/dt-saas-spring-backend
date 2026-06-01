package com.dtsaas.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DtSaasBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DtSaasBackendApplication.class, args);
	}

}
