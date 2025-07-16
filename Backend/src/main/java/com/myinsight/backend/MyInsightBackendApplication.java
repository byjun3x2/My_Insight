package com.myinsight.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyInsightBackendApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();  // .env 파일 로드

		// null 체크 추가
		String dbUsername = dotenv.get("DB_USERNAME");
		if (dbUsername == null) {
			throw new IllegalArgumentException("환경 변수 DB_USERNAME이 설정되지 않았습니다. .env 파일을 확인하세요.");
		}

		String dbPassword = dotenv.get("DB_PASSWORD");
		if (dbPassword == null) {
			throw new IllegalArgumentException("환경 변수 DB_PASSWORD가 설정되지 않았습니다. .env 파일을 확인하세요.");
		}

		System.setProperty("DB_USERNAME", dbUsername);
		System.setProperty("DB_PASSWORD", dbPassword);

		SpringApplication.run(MyInsightBackendApplication.class, args);
	}
}
