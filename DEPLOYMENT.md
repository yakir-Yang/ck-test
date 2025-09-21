# ClickHouse测试应用部署说明

## 📦 打包文件

已成功生成可执行jar包：
- **文件路径**: `target/clickhouse-test-1.0.0.jar`
- **文件大小**: 约20MB
- **包含内容**: 所有依赖和可执行代码

## 🚀 部署步骤

### 1. 上传jar包到服务器
```bash
# 将jar包上传到服务器
scp target/clickhouse-test-1.0.0.jar user@your-server:/path/to/deploy/
```

### 2. 在服务器上运行
```bash
# 进入部署目录
cd /path/to/deploy/

# 运行应用（默认端口8080）
java -jar clickhouse-test-1.0.0.jar

# 或者指定端口运行
java -jar clickhouse-test-1.0.0.jar --server.port=8080

# 后台运行
nohup java -jar clickhouse-test-1.0.0.jar > app.log 2>&1 &
```

### 3. 验证应用启动
```bash
# 检查应用是否启动成功
curl http://localhost:8080/api/clickhouse/test-connection

# 或者检查进程
ps aux | grep clickhouse-test
```

## 🔧 配置说明

### ClickHouse连接配置
应用已配置您提供的ClickHouse连接信息：
- **主机**: 10.151.0.15:8123
- **数据库**: saas_shoplus_analysis
- **用户名**: admin
- **密码**: gPXaYNwQv1PsJuG

### 应用配置
- **端口**: 8080（可通过 `--server.port` 参数修改）
- **连接池**: HikariCP，已优化配置
- **日志级别**: DEBUG（可在application.yml中调整）

## 📋 API接口使用

### 1. 测试ClickHouse连通性
```bash
curl -X GET http://your-server:8080/api/clickhouse/test-connection
```

### 2. 执行SQL文件
```bash
curl -X POST "http://your-server:8080/api/clickhouse/execute-sql-file?filePath=/path/to/your/sql/file.sql"
```

### 3. 上传并执行SQL文件
```bash
curl -X POST -F "file=@/path/to/your/sql/file.sql" http://your-server:8080/api/clickhouse/upload-and-execute
```

### 4. 执行单个SQL语句
```bash
curl -X POST http://your-server:8080/api/clickhouse/execute-sql \
  -H "Content-Type: application/json" \
  -d '{"sql": "SELECT 1 as test"}'
```

### 5. 获取数据库信息
```bash
curl -X GET http://your-server:8080/api/clickhouse/database-info
```

## 🛠️ 故障排除

### 1. 应用无法启动
- 检查Java版本（需要Java 8+）
- 检查端口是否被占用
- 查看应用日志

### 2. ClickHouse连接失败
- 确认ClickHouse服务器可访问
- 检查网络连接
- 验证用户名密码是否正确

### 3. LZ4压缩错误
如果遇到 `LZ4 is not supported` 错误，应用已配置禁用压缩：
- `compress=0` - 禁用压缩
- `decompress=0` - 禁用解压缩
- 这样可以避免LZ4库依赖问题

### 3. 查看应用日志
```bash
# 如果使用nohup运行
tail -f app.log

# 或者直接运行查看控制台输出
java -jar clickhouse-test-1.0.0.jar
```

## 📁 项目文件结构

```
clickhouse-test/
├── target/
│   └── clickhouse-test-1.0.0.jar    # 可执行jar包
├── src/main/resources/
│   └── application.yml              # 应用配置
├── test.sql                         # 测试SQL文件
├── README.md                        # 项目说明
└── DEPLOYMENT.md                    # 部署说明（本文件）
```

## 🔍 测试建议

1. **连通性测试**: 先测试ClickHouse连接是否正常
2. **SQL文件测试**: 使用提供的test.sql文件进行测试
3. **功能验证**: 逐一测试各个API接口
4. **性能测试**: 测试大量SQL执行性能

## 📞 技术支持

如果遇到问题，请检查：
1. 应用日志输出
2. ClickHouse服务器状态
3. 网络连接情况
4. 配置文件设置
