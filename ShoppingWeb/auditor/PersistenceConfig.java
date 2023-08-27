package com.shoppingweb.ShoppingWeb.auditor;

import com.shoppingweb.ShoppingWeb.entity.AppUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//look up current authenticated user
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfig{
    @Bean
    public AuditorAware<AppUser> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
