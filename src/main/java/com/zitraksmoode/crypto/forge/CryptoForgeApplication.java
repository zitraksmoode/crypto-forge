package com.zitraksmoode.crypto.forge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CryptoForgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoForgeApplication.class, args);
	}

}
