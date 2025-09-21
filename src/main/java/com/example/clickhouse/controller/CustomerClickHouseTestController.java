package com.example.clickhouse.controller;

import com.example.clickhouse.entry.SqlTemplate;
import com.example.clickhouse.entry.resp.QueryResultResp;
import com.example.clickhouse.service.ClickHouseQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/customer")
public class CustomerClickHouseTestController {

    /**
     * 测试客户代码连接
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testCustomerConnection() {
        try {
            Properties pro = createProperties();
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            SqlTemplate sqlTemplate = new SqlTemplate("SELECT 1 as test_number, 'Hello ClickHouse' as test_string");
            QueryResultResp result = service.query(sqlTemplate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "客户代码连接测试成功");
            response.put("data", result.getData());
            return ResponseEntity.ok(response);
        } catch (Throwable e) {
            log.error("测试客户代码连接失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "测试客户代码连接失败: " + e.getMessage());
            return ResponseEntity.ok(errorResult);
        }
    }

    /**
     * 测试客户代码查询
     */
    @PostMapping("/test-query")
    public ResponseEntity<Map<String, Object>> testCustomerQuery(@RequestBody Map<String, String> request) {
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", "SQL不能为空");
                return ResponseEntity.ok(errorResult);
            }

            Properties pro = createProperties();
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            SqlTemplate sqlTemplate = new SqlTemplate(sql);
            QueryResultResp result = service.query(sqlTemplate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "客户代码查询执行成功");
            response.put("data", result.getData());
            response.put("totalSize", result.getTotalSize());
            return ResponseEntity.ok(response);
        } catch (Throwable e) {
            log.error("测试客户代码查询失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "测试客户代码查询失败: " + e.getMessage());
            return ResponseEntity.ok(errorResult);
        }
    }

    /**
     * 测试客户代码执行SQL文件
     */
    @PostMapping("/execute-sql-file")
    public ResponseEntity<Map<String, Object>> testCustomerExecuteSqlFile(@RequestParam("filePath") String filePath) {
        try {
            log.info("开始执行客户代码SQL文件: {}", filePath);
            
            Properties pro = createProperties();
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            String sqlContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
            log.info("读取SQL文件: {}, 内容长度: {}", filePath, sqlContent.length());

            String[] sqlStatements = sqlContent.split(";");
            
            Map<String, Object> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("totalStatements", sqlStatements.length);
            result.put("executionResults", new ArrayList<>());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> executionResults = (List<Map<String, Object>>) result.get("executionResults");

            for (int i = 0; i < sqlStatements.length; i++) {
                String sql = sqlStatements[i].trim();
                if (sql.isEmpty()) continue;

                Map<String, Object> statementResult = new HashMap<>();
                statementResult.put("statementIndex", i + 1);
                statementResult.put("sql", sql);

                try {
                    long startTime = System.currentTimeMillis();
                    
                    if (sql.toLowerCase().trim().startsWith("select")) {
                        SqlTemplate sqlTemplate = new SqlTemplate(sql);
                        QueryResultResp queryResult = service.query(sqlTemplate);
                        statementResult.put("type", "SELECT");
                        statementResult.put("rowCount", queryResult.getTotalSize());
                        statementResult.put("data", queryResult.getData());
                    } else {
                        SqlTemplate sqlTemplate = new SqlTemplate(sql);
                        service.exec(sqlTemplate);
                        statementResult.put("type", "UPDATE");
                        statementResult.put("affectedRows", 1);
                    }
                    
                    long endTime = System.currentTimeMillis();
                    statementResult.put("executionTime", endTime - startTime);
                    statementResult.put("success", true);
                    
                    log.info("客户代码SQL语句 {} 执行成功，耗时: {}ms", i + 1, endTime - startTime);
                    
                } catch (Throwable e) {
                    statementResult.put("success", false);
                    statementResult.put("error", e.getMessage());
                    log.error("客户代码SQL语句 {} 执行失败: {}", i + 1, e.getMessage());
                }

                executionResults.add(statementResult);
            }

            result.put("success", true);
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            log.error("读取SQL文件失败: {}", filePath, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "读取SQL文件失败: " + e.getMessage());
            errorResult.put("filePath", filePath);
            return ResponseEntity.ok(errorResult);
        } catch (Exception e) {
            log.error("执行客户代码SQL文件失败: {}", filePath, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "执行客户代码SQL文件失败: " + e.getMessage());
            errorResult.put("filePath", filePath);
            return ResponseEntity.ok(errorResult);
        }
    }

    private Properties createProperties() {
        Properties pro = new Properties();
        pro.setProperty("url", "jdbc:clickhouse://10.151.0.15:8123/saas_shoplus_analysis?allow_multiple_queries=true&query_settings=allow_multi_statements=1&allowMultiQueries=true");
        pro.setProperty("user", "admin");
        pro.setProperty("pw", "gPXaYNwQv1PsJuG");
        
        if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
            pro.setProperty("plusTime", System.getProperty("plusTime"));
        }
        if (System.getProperty("user") != null && !"".equals(System.getProperty("user"))) {
            pro.setProperty("user", System.getProperty("user"));
        }
        if (System.getProperty("pw") != null && !"".equals(System.getProperty("pw"))) {
            pro.setProperty("pw", System.getProperty("pw"));
        }
        
        return pro;
    }
}