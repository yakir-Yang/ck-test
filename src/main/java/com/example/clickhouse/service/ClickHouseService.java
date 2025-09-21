package com.example.clickhouse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ClickHouseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试ClickHouse连通性
     */
    public boolean testConnection() {
        try {
            // 执行简单查询测试连接
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            log.info("ClickHouse连接测试成功，查询结果: {}", result);
            return true;
        } catch (Exception e) {
            log.error("ClickHouse连接测试失败", e);
            return false;
        }
    }

    /**
     * 获取连接信息
     */
    public Map<String, Object> getConnectionInfo() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            Map<String, Object> info = new java.util.HashMap<>();
            info.put("url", connection.getMetaData().getURL());
            info.put("username", connection.getMetaData().getUserName());
            info.put("databaseProductName", connection.getMetaData().getDatabaseProductName());
            info.put("databaseProductVersion", connection.getMetaData().getDatabaseProductVersion());
            info.put("driverName", connection.getMetaData().getDriverName());
            info.put("driverVersion", connection.getMetaData().getDriverVersion());
            info.put("isReadOnly", connection.isReadOnly());
            info.put("autoCommit", connection.getAutoCommit());
            return info;
        } catch (SQLException e) {
            log.error("获取连接信息失败", e);
            throw new RuntimeException("获取连接信息失败: " + e.getMessage());
        }
    }

    /**
     * 执行SQL文件
     */
    public Map<String, Object> executeSqlFile(String filePath) {
        try {
            // 读取SQL文件内容
            String sqlContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
            log.info("读取SQL文件: {}, 内容长度: {}", filePath, sqlContent.length());

            // 分割SQL语句（以分号分割）
            String[] sqlStatements = sqlContent.split(";");
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("filePath", filePath);
            result.put("totalStatements", sqlStatements.length);
            result.put("executionResults", new java.util.ArrayList<>());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> executionResults = (List<Map<String, Object>>) result.get("executionResults");

            // 执行每个SQL语句
            for (int i = 0; i < sqlStatements.length; i++) {
                String sql = sqlStatements[i].trim();
                if (sql.isEmpty()) {
                    continue;
                }

                Map<String, Object> statementResult = new java.util.HashMap<>();
                statementResult.put("statementIndex", i + 1);
                statementResult.put("sql", sql);

                try {
                    long startTime = System.currentTimeMillis();
                    
                    if (sql.toLowerCase().trim().startsWith("select")) {
                        // 查询语句
                        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(sql);
                        statementResult.put("type", "SELECT");
                        statementResult.put("rowCount", queryResult.size());
                        statementResult.put("data", queryResult);
                    } else {
                        // 更新语句
                        int updateCount = jdbcTemplate.update(sql);
                        statementResult.put("type", "UPDATE");
                        statementResult.put("affectedRows", updateCount);
                    }
                    
                    long endTime = System.currentTimeMillis();
                    statementResult.put("executionTime", endTime - startTime);
                    statementResult.put("success", true);
                    
                    log.info("SQL语句 {} 执行成功，耗时: {}ms", i + 1, endTime - startTime);
                    
                } catch (Exception e) {
                    statementResult.put("success", false);
                    statementResult.put("error", e.getMessage());
                    log.error("SQL语句 {} 执行失败: {}", i + 1, e.getMessage());
                }

                executionResults.add(statementResult);
            }

            result.put("success", true);
            return result;

        } catch (IOException e) {
            log.error("读取SQL文件失败: {}", filePath, e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "读取SQL文件失败: " + e.getMessage());
            errorResult.put("filePath", filePath);
            return errorResult;
        } catch (Exception e) {
            log.error("执行SQL文件失败: {}", filePath, e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "执行SQL文件失败: " + e.getMessage());
            errorResult.put("filePath", filePath);
            return errorResult;
        }
    }

    /**
     * 执行单个SQL语句
     */
    public Map<String, Object> executeSql(String sql) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("sql", sql);
            
            if (sql.toLowerCase().trim().startsWith("select")) {
                // 查询语句
                List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(sql);
                result.put("type", "SELECT");
                result.put("rowCount", queryResult.size());
                result.put("data", queryResult);
            } else {
                // 更新语句
                int updateCount = jdbcTemplate.update(sql);
                result.put("type", "UPDATE");
                result.put("affectedRows", updateCount);
            }
            
            long endTime = System.currentTimeMillis();
            result.put("executionTime", endTime - startTime);
            result.put("success", true);
            
            return result;
            
        } catch (Exception e) {
            log.error("执行SQL失败: {}", sql, e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", e.getMessage());
            errorResult.put("sql", sql);
            return errorResult;
        }
    }
}
