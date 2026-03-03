package com.luis.spring.security.msauth_poc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Luis Balarezo
 **/
@Configuration
@EnableJpaAuditing
@EnableScheduling
public class AuditingConfig {
}
