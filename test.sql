-- ClickHouse测试SQL文件
-- 这个文件包含一些测试SQL语句

-- 测试基本查询
SELECT 1 as test_number, 'Hello ClickHouse' as test_string;

-- 测试当前时间
SELECT now() as current_time;

-- 测试数据库信息
SELECT name, engine, data_paths 
FROM system.databases 
WHERE name = 'saas_shoplus_analysis';

-- 测试表列表（如果存在）
SELECT name, engine, total_rows 
FROM system.tables 
WHERE database = 'saas_shoplus_analysis' 
LIMIT 10;
