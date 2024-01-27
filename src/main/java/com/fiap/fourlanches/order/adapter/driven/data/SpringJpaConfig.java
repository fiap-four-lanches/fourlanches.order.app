package com.fiap.fourlanches.order.adapter.driven.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.fiap.fourlanches.order.adapter.driven.data")
public class SpringJpaConfig {
}
