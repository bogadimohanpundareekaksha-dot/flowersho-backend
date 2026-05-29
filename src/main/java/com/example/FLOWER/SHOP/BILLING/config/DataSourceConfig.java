package com.example.FLOWER.SHOP.BILLING.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        String rawUrl = properties.getUrl();
        if (rawUrl == null) {
            rawUrl = System.getenv("DATABASE_URL");
        }

        if (rawUrl != null) {
            if ((rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://") || rawUrl.startsWith("mysql://")) 
                    && rawUrl.contains("@")) {
                try {
                    String uriString = rawUrl;
                    if (rawUrl.startsWith("postgresql://")) {
                        uriString = rawUrl.replaceFirst("^postgresql://", "postgres://");
                    }
                    
                    URI dbUri = new URI(uriString);
                    String username = null;
                    String password = null;
                    
                    if (dbUri.getUserInfo() != null) {
                        String[] userInfo = dbUri.getUserInfo().split(":");
                        username = userInfo[0];
                        if (userInfo.length > 1) {
                            password = userInfo[1];
                        }
                    }
                    
                    String host = dbUri.getHost();
                    int port = dbUri.getPort();
                    String path = dbUri.getPath();
                    
                    String dbType = rawUrl.startsWith("mysql") ? "mysql" : "postgresql";
                    String jdbcUrl;
                    if (port != -1) {
                        jdbcUrl = "jdbc:" + dbType + "://" + host + ":" + port + path;
                    } else {
                        jdbcUrl = "jdbc:" + dbType + "://" + host + path;
                    }
                    
                    if (dbUri.getQuery() != null) {
                        jdbcUrl += "?" + dbUri.getQuery();
                    }
                    
                    properties.setUrl(jdbcUrl);
                    if (username != null) {
                        properties.setUsername(username);
                    }
                    if (password != null) {
                        properties.setPassword(password);
                    }
                    
                    if (dbType.equals("postgresql")) {
                        properties.setDriverClassName("org.postgresql.Driver");
                    } else {
                        properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    }
                    
                } catch (URISyntaxException e) {
                    fallbackReplace(properties, rawUrl);
                }
            } else {
                fallbackReplace(properties, rawUrl);
            }
        }
        return properties.initializeDataSourceBuilder().build();
    }

    private void fallbackReplace(DataSourceProperties properties, String rawUrl) {
        if (rawUrl.startsWith("postgres://")) {
            properties.setUrl(rawUrl.replaceFirst("^postgres://", "jdbc:postgresql://"));
        } else if (rawUrl.startsWith("postgresql://")) {
            properties.setUrl(rawUrl.replaceFirst("^postgresql://", "jdbc:postgresql://"));
        } else if (rawUrl.startsWith("mysql://")) {
            properties.setUrl(rawUrl.replaceFirst("^mysql://", "jdbc:mysql://"));
        }
    }
}
