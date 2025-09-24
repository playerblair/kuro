package dev.playerblair.kuro;

import dev.playerblair.kuro.security.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class KuroApplication {

	public static void main(String[] args) {
		SpringApplication.run(KuroApplication.class, args);
	}

}
