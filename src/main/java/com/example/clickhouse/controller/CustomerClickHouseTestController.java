package com.example.clickhouse.controller;

import com.example.clickhouse.entry.SqlTemplate;
import com.example.clickhouse.entry.req.DataCopyReq;
import com.example.clickhouse.entry.resp.ColumnMetaResp;
import com.example.clickhouse.entry.resp.QueryResultResp;
import com.example.clickhouse.service.ClickHouseQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/customer-clickhouse")
public class CustomerClickHouseTestController {

    /**
     * 测试客户代码的连接
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testCustomerConnection() {
        try {
            // 创建Properties配置
            Properties pro = new Properties();
            pro.setProperty("url", "jdbc:clickhouse://10.151.0.15:8123/saas_shoplus_analysis?allow_multiple_queries=true&query_settings=allow_multi_statements=1&allowMultiQueries=true");
            pro.setProperty("user", "admin");
            pro.setProperty("pw", "gPXaYNwQv1PsJuG");
            
            // 根据系统属性设置plusTime
            if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
                pro.setProperty("plusTime", System.getProperty("plusTime"));
            }

            // 创建客户的服务实例
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            // 测试简单查询
            SqlTemplate sqlTemplate = new SqlTemplate("SELECT 1 as test_number, 'Hello ClickHouse' as test_string");
            QueryResultResp result;
            try {
                result = service.query(sqlTemplate);
            } catch (Throwable e) {
                throw new RuntimeException("连接测试失败", e);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "客户代码连接测试成功");
            response.put("data", result.getData());
            response.put("totalSize", result.getTotalSize());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("客户代码连接测试失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "客户代码连接测试失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 测试客户代码的查询功能
     */
    @PostMapping("/test-query")
    public ResponseEntity<Map<String, Object>> testCustomerQuery(@RequestBody Map<String, String> request) {
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 创建Properties配置
            Properties pro = new Properties();
            pro.setProperty("url", "jdbc:clickhouse://10.151.0.15:8123/saas_shoplus_analysis?allow_multiple_queries=true&query_settings=allow_multi_statements=1&allowMultiQueries=true");
            pro.setProperty("user", "admin");
            pro.setProperty("pw", "gPXaYNwQv1PsJuG");
            
            // 根据系统属性设置plusTime
            if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
                pro.setProperty("plusTime", System.getProperty("plusTime"));
            }

            // 创建客户的服务实例
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            // 执行查询
            SqlTemplate sqlTemplate = new SqlTemplate(sql);
            QueryResultResp result;
            try {
                result = service.query(sqlTemplate);
            } catch (Throwable e) {
                throw new RuntimeException("查询执行失败", e);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "客户代码查询执行成功");
            response.put("data", result.getData());
            response.put("totalSize", result.getTotalSize());
            response.put("sql", sql);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("客户代码查询执行失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "客户代码查询执行失败: " + e.getMessage());
            errorResponse.put("sql", request.get("sql"));
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 测试客户代码的执行功能
     */
    @PostMapping("/test-exec")
    public ResponseEntity<Map<String, Object>> testCustomerExec(@RequestBody Map<String, String> request) {
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 创建Properties配置
            Properties pro = new Properties();
            pro.setProperty("url", "jdbc:clickhouse://10.151.0.15:8123/saas_shoplus_analysis?allow_multiple_queries=true&query_settings=allow_multi_statements=1&allowMultiQueries=true");
            pro.setProperty("user", "admin");
            pro.setProperty("pw", "gPXaYNwQv1PsJuG");
            
            // 根据系统属性设置plusTime
            if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
                pro.setProperty("plusTime", System.getProperty("plusTime"));
            }

            // 创建客户的服务实例
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            // 执行SQL
            SqlTemplate sqlTemplate = new SqlTemplate(sql);
            try {
                service.exec(sqlTemplate);
            } catch (Throwable e) {
                throw new RuntimeException("SQL执行失败", e);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "客户代码执行成功");
            response.put("sql", sql);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("客户代码执行失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "客户代码执行失败: " + e.getMessage());
            errorResponse.put("sql", request.get("sql"));
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 测试客户代码的计数查询功能
     */
    @PostMapping("/test-count")
    public ResponseEntity<Map<String, Object>> testCustomerCount(@RequestBody Map<String, String> request) {
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 创建Properties配置
            Properties pro = new Properties();
            pro.setProperty("url", "jdbc:clickhouse://10.151.0.15:8123/saas_shoplus_analysis?allow_multiple_queries=true&query_settings=allow_multi_statements=1&allowMultiQueries=true");
            pro.setProperty("user", "admin");
            pro.setProperty("pw", "gPXaYNwQv1PsJuG");
            
            // 根据系统属性设置plusTime
            if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
                pro.setProperty("plusTime", System.getProperty("plusTime"));
            }

            // 创建客户的服务实例
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            // 执行计数查询
            QueryResultResp result = service.queryCount(sql);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "客户代码计数查询执行成功");
            response.put("totalSize", result.getTotalSize());
            response.put("exceptionMsg", result.getExceptionMsg());
            response.put("sql", sql);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("客户代码计数查询执行失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "客户代码计数查询执行失败: " + e.getMessage());
            errorResponse.put("sql", request.get("sql"));
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 测试客户代码的插入功能
     */
    @PostMapping("/test-insert")
    public ResponseEntity<Map<String, Object>> testCustomerInsert() {
        try {
            // 创建Properties配置
            Properties pro = new Properties();
            pro.setProperty("url", "jdbc:clickhouse://10.151.0.15:8123/saas_shoplus_analysis?allow_multiple_queries=true&query_settings=allow_multi_statements=1&allowMultiQueries=true");
            pro.setProperty("user", "admin");
            pro.setProperty("pw", "gPXaYNwQv1PsJuG");
            
            // 根据系统属性设置plusTime
            if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
                pro.setProperty("plusTime", System.getProperty("plusTime"));
            }

            // 创建客户的服务实例
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            // 创建测试数据
            DataCopyReq req = new DataCopyReq("test_db", "test_table");
            List<ColumnMetaResp> columnMetas = Arrays.asList(
                new ColumnMetaResp("id", "Int32"),
                new ColumnMetaResp("name", "String"),
                new ColumnMetaResp("age", "Int32")
            );
            List<List<Object>> results = Arrays.asList(
                Arrays.asList(1, "张三", 25),
                Arrays.asList(2, "李四", 30),
                Arrays.asList(3, "王五", 35)
            );
            
            // 执行插入
            service.insert(req, columnMetas, results);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "客户代码插入执行成功");
            response.put("insertedRows", results.size());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("客户代码插入执行失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "客户代码插入执行失败: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 测试客户代码执行SQL文件
     */
    @PostMapping("/execute-sql-file")
    public ResponseEntity<Map<String, Object>> testCustomerExecuteSqlFile(@RequestParam("filePath") String filePath) {
        try {
            log.info("开始执行客户代码SQL文件: {}", filePath);
            
            // 创建Properties配置
            Properties pro = new Properties();
            pro.setProperty("url", "jdbc:clickhouse://10.151.0.15:8123/saas_shoplus_analysis?allow_multiple_queries=true&query_settings=allow_multi_statements=1&allowMultiQueries=true");
            pro.setProperty("user", "admin");
            pro.setProperty("pw", "gPXaYNwQv1PsJuG");
            
            // 根据系统属性设置plusTime
            if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
                pro.setProperty("plusTime", System.getProperty("plusTime"));
            }

            // 创建客户的服务实例
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            // 读取SQL文件内容
            String sqlContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
            log.info("读取SQL文件: {}, 内容长度: {}", filePath, sqlContent.length());

            // 分割SQL语句（以分号分割）
            String[] sqlStatements = sqlContent.split(";");
            
            Map<String, Object> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("totalStatements", sqlStatements.length);
            result.put("executionResults", new ArrayList<>());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> executionResults = (List<Map<String, Object>>) result.get("executionResults");

            // 执行每个SQL语句
            for (int i = 0; i < sqlStatements.length; i++) {
                String sql = sqlStatements[i].trim();
                if (sql.isEmpty()) {
                    continue;
                }

                Map<String, Object> statementResult = new HashMap<>();
                statementResult.put("statementIndex", i + 1);
                statementResult.put("sql", sql);

                try {
                    long startTime = System.currentTimeMillis();
                    
                    if (sql.toLowerCase().trim().startsWith("select")) {
                        // 查询语句
                        SqlTemplate sqlTemplate = new SqlTemplate(sql);
                        QueryResultResp queryResult = service.query(sqlTemplate);
                        statementResult.put("type", "SELECT");
                        statementResult.put("rowCount", queryResult.getTotalSize());
                        statementResult.put("data", queryResult.getData());
                    } else {
                        // 更新语句
                        SqlTemplate sqlTemplate = new SqlTemplate(sql);
                        service.exec(sqlTemplate);
                        statementResult.put("type", "UPDATE");
                        statementResult.put("affectedRows", 1); // 客户代码没有返回影响行数
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

    /**
     * 测试客户代码上传并执行SQL文件
     */
    @PostMapping("/upload-and-execute")
    public ResponseEntity<Map<String, Object>> testCustomerUploadAndExecuteSqlFile(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", "文件不能为空");
                return ResponseEntity.badRequest().body(errorResult);
            }

            // 保存上传的文件到临时目录
            String originalFilename = file.getOriginalFilename();
            String tempDir = System.getProperty("java.io.tmpdir");
            java.nio.file.Path tempFilePath = Paths.get(tempDir, "customer_clickhouse_" + System.currentTimeMillis() + "_" + originalFilename);
            
            Files.write(tempFilePath, file.getBytes());
            log.info("客户代码文件已保存到: {}", tempFilePath);

            // 执行SQL文件
            Map<String, Object> result = new HashMap<>();
            result.put("filePath", tempFilePath.toString());
            result.put("originalFilename", originalFilename);
            
            // 创建Properties配置
            Properties pro = new Properties();
            pro.setProperty("url", "jdbc:clickhouse://10.151.0.15:8123/saas_shoplus_analysis?allow_multiple_queries=true&query_settings=allow_multi_statements=1&allowMultiQueries=true");
            pro.setProperty("user", "admin");
            pro.setProperty("pw", "gPXaYNwQv1PsJuG");
            
            // 根据系统属性设置plusTime
            if (System.getProperty("plusTime") != null && !"".equals(System.getProperty("plusTime"))) {
                pro.setProperty("plusTime", System.getProperty("plusTime"));
            }

            // 创建客户的服务实例
            ClickHouseQueryService service = new ClickHouseQueryService(pro);
            
            // 读取SQL文件内容
            String sqlContent = new String(Files.readAllBytes(tempFilePath), "UTF-8");
            log.info("读取客户代码SQL文件: {}, 内容长度: {}", tempFilePath, sqlContent.length());

            // 分割SQL语句（以分号分割）
            String[] sqlStatements = sqlContent.split(";");
            
            result.put("totalStatements", sqlStatements.length);
            result.put("executionResults", new ArrayList<>());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> executionResults = (List<Map<String, Object>>) result.get("executionResults");

            // 执行每个SQL语句
            for (int i = 0; i < sqlStatements.length; i++) {
                String sql = sqlStatements[i].trim();
                if (sql.isEmpty()) {
                    continue;
                }

                Map<String, Object> statementResult = new HashMap<>();
                statementResult.put("statementIndex", i + 1);
                statementResult.put("sql", sql);

                try {
                    long startTime = System.currentTimeMillis();
                    
                    if (sql.toLowerCase().trim().startsWith("select")) {
                        // 查询语句
                        SqlTemplate sqlTemplate = new SqlTemplate(sql);
                        QueryResultResp queryResult = service.query(sqlTemplate);
                        statementResult.put("type", "SELECT");
                        statementResult.put("rowCount", queryResult.getTotalSize());
                        statementResult.put("data", queryResult.getData());
                    } else {
                        // 更新语句
                        SqlTemplate sqlTemplate = new SqlTemplate(sql);
                        service.exec(sqlTemplate);
                        statementResult.put("type", "UPDATE");
                        statementResult.put("affectedRows", 1); // 客户代码没有返回影响行数
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
            
            // 清理临时文件
            try {
                Files.deleteIfExists(tempFilePath);
                log.info("客户代码临时文件已删除: {}", tempFilePath);
            } catch (IOException e) {
                log.warn("删除客户代码临时文件失败: {}", tempFilePath, e);
            }

            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("客户代码上传并执行SQL文件失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "客户代码上传并执行SQL文件失败: " + e.getMessage());
            return ResponseEntity.ok(errorResult);
        }
    }
}
