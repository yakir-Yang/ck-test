# ClickHouse连接测试应用

这是一个Spring Boot应用程序，用于测试ClickHouse数据库连接和执行SQL文件。

## 功能特性

- ✅ 使用HikariCP连接池连接ClickHouse
- ✅ 测试ClickHouse连通性
- ✅ 支持执行SQL文件
- ✅ 支持上传并执行SQL文件
- ✅ 支持执行单个SQL语句
- ✅ 获取数据库连接信息

## 技术栈

- Spring Boot 2.7.18
- HikariCP 连接池
- ClickHouse JDBC驱动
- Maven构建工具

## 配置信息

ClickHouse连接配置：
- 主机: 10.151.0.15:8123
- 数据库: saas_shoplus_analysis
- 用户名: admin
- 密码: gPXaYNwQv1PsJuG

## 快速开始

### 1. 编译项目
```bash
mvn clean compile
```

### 2. 运行应用
```bash
mvn spring-boot:run
```

应用将在 http://localhost:8080 启动

### 3. 测试API

#### 测试连通性
```bash
curl -X GET http://localhost:8080/api/clickhouse/test-connection
```

#### 执行SQL文件
```bash
curl -X POST "http://localhost:8080/api/clickhouse/execute-sql-file?filePath=/path/to/your/sql/file.sql"
```

#### 上传并执行SQL文件
```bash
curl -X POST -F "file=@/path/to/your/sql/file.sql" http://localhost:8080/api/clickhouse/upload-and-execute
```

#### 执行单个SQL语句
```bash
curl -X POST http://localhost:8080/api/clickhouse/execute-sql \
  -H "Content-Type: application/json" \
  -d '{"sql": "SELECT 1 as test"}'
```

#### 获取数据库信息
```bash
curl -X GET http://localhost:8080/api/clickhouse/database-info
```

## API接口说明

### 1. 测试连通性
- **URL**: `GET /api/clickhouse/test-connection`
- **功能**: 测试ClickHouse数据库连接
- **返回**: 连接状态和连接信息

### 2. 执行SQL文件
- **URL**: `POST /api/clickhouse/execute-sql-file`
- **参数**: `filePath` - SQL文件路径
- **功能**: 执行指定路径的SQL文件

### 3. 上传并执行SQL文件
- **URL**: `POST /api/clickhouse/upload-and-execute`
- **参数**: `file` - 上传的SQL文件
- **功能**: 上传SQL文件并执行

### 4. 执行单个SQL语句
- **URL**: `POST /api/clickhouse/execute-sql`
- **参数**: `{"sql": "SQL语句"}`
- **功能**: 执行单个SQL语句

### 5. 获取数据库信息
- **URL**: `GET /api/clickhouse/database-info`
- **功能**: 获取数据库连接信息

## 项目结构

```
src/
├── main/
│   ├── java/com/example/clickhouse/
│   │   ├── ClickhouseTestApplication.java    # 主启动类
│   │   ├── config/
│   │   │   └── ClickHouseConfig.java         # ClickHouse配置
│   │   ├── controller/
│   │   │   └── ClickHouseController.java     # REST控制器
│   │   └── service/
│   │       └── ClickHouseService.java        # 业务服务
│   └── resources/
│       └── application.yml                   # 应用配置
└── pom.xml                                   # Maven配置
```

## 注意事项

1. 确保ClickHouse服务器可访问
2. SQL文件中的语句以分号(;)分割
3. 支持SELECT查询和UPDATE/INSERT/DELETE等更新操作
4. 上传的文件会保存到临时目录，执行后自动删除
5. 建议在生产环境中调整HikariCP连接池参数
