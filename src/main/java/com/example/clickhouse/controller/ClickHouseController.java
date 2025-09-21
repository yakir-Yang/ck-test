package com.example.clickhouse.controller;

import com.example.clickhouse.service.ClickHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/clickhouse")
public class ClickHouseController {

    @Autowired
    private ClickHouseService clickHouseService;

    /**
     * 测试ClickHouse连通性
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            boolean isConnected = clickHouseService.testConnection();
            Map<String, Object> connectionInfo = clickHouseService.getConnectionInfo();
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", isConnected);
            result.put("message", isConnected ? "ClickHouse连接成功" : "ClickHouse连接失败");
            result.put("connectionInfo", connectionInfo);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("测试连接失败", e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "测试连接失败: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(errorResult);
        }
    }

    /**
     * 执行SQL文件
     */
    @PostMapping("/execute-sql-file")
    public ResponseEntity<Map<String, Object>> executeSqlFile(@RequestParam("filePath") String filePath) {
        try {
            log.info("开始执行SQL文件: {}", filePath);
            Map<String, Object> result = clickHouseService.executeSqlFile(filePath);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("执行SQL文件失败", e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "执行SQL文件失败: " + e.getMessage());
            errorResult.put("filePath", filePath);
            return ResponseEntity.ok(errorResult);
        }
    }

    /**
     * 上传并执行SQL文件
     */
    @PostMapping("/upload-and-execute")
    public ResponseEntity<Map<String, Object>> uploadAndExecuteSqlFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, Object> errorResult = new java.util.HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", "文件不能为空");
                return ResponseEntity.badRequest().body(errorResult);
            }

            // 保存上传的文件到临时目录
            String originalFilename = file.getOriginalFilename();
            String tempDir = System.getProperty("java.io.tmpdir");
            Path tempFilePath = Paths.get(tempDir, "clickhouse_" + System.currentTimeMillis() + "_" + originalFilename);
            
            Files.write(tempFilePath, file.getBytes());
            log.info("文件已保存到: {}", tempFilePath);

            // 执行SQL文件
            Map<String, Object> result = clickHouseService.executeSqlFile(tempFilePath.toString());
            
            // 清理临时文件
            try {
                Files.deleteIfExists(tempFilePath);
                log.info("临时文件已删除: {}", tempFilePath);
            } catch (IOException e) {
                log.warn("删除临时文件失败: {}", tempFilePath, e);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("上传并执行SQL文件失败", e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "上传并执行SQL文件失败: " + e.getMessage());
            return ResponseEntity.ok(errorResult);
        }
    }

    /**
     * 执行单个SQL语句
     */
    @PostMapping("/execute-sql")
    public ResponseEntity<Map<String, Object>> executeSql(@RequestBody Map<String, String> request) {
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> errorResult = new java.util.HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(errorResult);
            }

            log.info("开始执行SQL: {}", sql);
            Map<String, Object> result = clickHouseService.executeSql(sql);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("执行SQL失败", e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "执行SQL失败: " + e.getMessage());
            return ResponseEntity.ok(errorResult);
        }
    }

    /**
     * 获取数据库信息
     */
    @GetMapping("/database-info")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        try {
            Map<String, Object> connectionInfo = clickHouseService.getConnectionInfo();
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("data", connectionInfo);
            result.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取数据库信息失败", e);
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "获取数据库信息失败: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(errorResult);
        }
    }
}
