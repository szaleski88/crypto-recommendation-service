package com.szaleski.xmcy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CryptoRecommendationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoRecommendationApplication.class, args);
	}

}
