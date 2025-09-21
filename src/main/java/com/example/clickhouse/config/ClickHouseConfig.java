package com.example.clickhouse.config;

import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;

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
        // 创建ClickHouse属性配置
        Properties ckProperties = new Properties();
        
        // 根据plusTime属性设置超时时间
        if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
            ckProperties.setProperty("dataTransferTimeout", String.valueOf(30 * 10000000));
            ckProperties.setProperty("maxExecutionTime", String.valueOf(30 * 10000000));
            ckProperties.setProperty("socketTimeout", String.valueOf(120 * 10000000));
        } else {
            ckProperties.setProperty("dataTransferTimeout", String.valueOf(30 * 1000));
            ckProperties.setProperty("maxExecutionTime", String.valueOf(30 * 1000));
            ckProperties.setProperty("socketTimeout", String.valueOf(120 * 1000));
        }
        
        // 设置用户和密码
        if (System.getProperty("user") != null && !"".equals(System.getProperty("user"))) {
            ckProperties.setProperty("user", System.getProperty("user"));
        } else {
            ckProperties.setProperty("user", username);
        }
        
        if (System.getProperty("pw") != null && !"".equals(System.getProperty("pw"))) {
            ckProperties.setProperty("password", System.getProperty("pw"));
        } else {
            ckProperties.setProperty("password", password);
        }
        
        // 设置URL
        ckProperties.setProperty("url", url);
        
        // 创建ClickHouse数据源
        ClickHouseDataSource ckDataSource;
        try {
            ckDataSource = new ClickHouseDataSource(url, ckProperties);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("创建ClickHouse数据源失败", e);
        }
        
        return ckDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource clickHouseDataSource) {
        return new JdbcTemplate(clickHouseDataSource);
    }
}
