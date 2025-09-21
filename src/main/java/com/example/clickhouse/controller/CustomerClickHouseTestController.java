package com.example.clickhouse.controller;

import com.example.clickhouse.entry.SqlTemplate;
import com.example.clickhouse.entry.req.DataCopyReq;
import com.example.clickhouse.entry.resp.ColumnMetaResp;
import com.example.clickhouse.entry.resp.QueryResultResp;
import com.example.clickhouse.service.ClickHouseQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
