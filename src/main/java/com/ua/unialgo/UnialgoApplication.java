package com.ua.unialgo;

import com.ua.unialgo.user.repository.TeacherRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = TeacherRepository.class)
public class UnialgoApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnialgoApplication.class, args);
	}
}
