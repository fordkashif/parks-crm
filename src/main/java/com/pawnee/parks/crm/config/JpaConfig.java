package com.pawnee.parks.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    Object principal = auth.getPrincipal();
                    String name = principal instanceof org.springframework.security.core.userdetails.User u
                            ? u.getUsername()
                            : auth.getName();
                    return Optional.ofNullable(name);
                }
            } catch (NoClassDefFoundError ignored) {
                // spring-security not on classpath -> fall through to default
            }
            return Optional.of("system"); // <— ensures not null in dev/tests
        };
    }
}
