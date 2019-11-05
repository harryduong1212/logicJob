package com.demo.LogicJob;

import com.demo.LogicJob.DAO.JpaAuditingConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class LogicJobApplication {
	@Bean
	public AuditorAware<String> auditorAware() {
		return (AuditorAware<String>) new JpaAuditingConfiguration();
	}

	public static void main(String[] args) {
		SpringApplication.run(LogicJobApplication.class, args);
	}

}
