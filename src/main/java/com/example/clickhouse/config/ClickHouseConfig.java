package com.example.clickhouse.config;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
        
        // 禁用压缩以避免LZ4错误
        ckProperties.setProperty("compress", "0");
        ckProperties.setProperty("decompress", "0");
        
        // 创建ClickHouse数据源
        ClickHouseDataSource ckDataSource;
        try {
            ckDataSource = new ClickHouseDataSource(url, ckProperties);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("创建ClickHouse数据源失败", e);
        }
        
        // 创建HikariCP连接池配置
        HikariConfig config = new HikariConfig();
        config.setDataSource(ckDataSource);
        
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
        
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource clickHouseDataSource) {
        return new JdbcTemplate(clickHouseDataSource);
    }
}
