package com.example.clickhouse.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ClickHouseConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource clickHouseDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        
        // HikariCP连接池配置
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(0); // 启动时不创建连接
        config.setPoolName("ClickHouseHikariCP");
        
        // 设置连接验证查询，避免启动时立即连接
        config.setConnectionTestQuery("SELECT 1");
        config.setInitializationFailTimeout(-1); // 不立即失败
        config.setLeakDetectionThreshold(0); // 禁用泄漏检测
        
        // ClickHouse特定配置
        config.addDataSourceProperty("allowMultiQueries", "true");
        config.addDataSourceProperty("allow_multiple_queries", "true");
        config.addDataSourceProperty("query_settings", "allow_multi_statements=1");
        
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource clickHouseDataSource) {
        return new JdbcTemplate(clickHouseDataSource);
    }
}
