package com.example.FLOWER.SHOP.BILLING.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl();
        if (url != null) {
            if (url.startsWith("postgres://")) {
                properties.setUrl(url.replaceFirst("^postgres://", "jdbc:postgresql://"));
            } else if (url.startsWith("postgresql://")) {
                properties.setUrl(url.replaceFirst("^postgresql://", "jdbc:postgresql://"));
            } else if (url.startsWith("mysql://")) {
                properties.setUrl(url.replaceFirst("^mysql://", "jdbc:mysql://"));
            }
        }
        return properties.initializeDataSourceBuilder().build();
    }
}
