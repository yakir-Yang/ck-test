package com.example.clickhouse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.clickhouse.entry.SqlTemplate;
import com.example.clickhouse.entry.req.DataCopyReq;
import com.example.clickhouse.entry.resp.ColumnMetaResp;
import com.example.clickhouse.entry.resp.QueryResultResp;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.calcite.sql.parser.SqlParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ClickHouseQueryService {
    private final static Logger logger = LoggerFactory.getLogger(ClickHouseQueryService.class);
    private JdbcTemplate jdbcTemplate;

    public ClickHouseQueryService(Properties pro){
        final HikariConfig config = new HikariConfig();
        final ClickHouseProperties ckProperties = new ClickHouseProperties();
        if (pro.getProperty("plusTime") != null && !"".equals(pro.getProperty("plusTime"))) {
            ckProperties.setDataTransferTimeout(30 * 10000000);
            ckProperties.setMaxExecutionTime(30 * 10000000);
            ckProperties.setSocketTimeout(120 * 10000000);
        } else {
            ckProperties.setDataTransferTimeout(30 * 1000);
            ckProperties.setMaxExecutionTime(30 * 1000);
            ckProperties.setSocketTimeout(120 * 1000);
        }
        if(pro.getProperty("user") != null && !"".equals(pro.getProperty("user"))){
            ckProperties.setUser(pro.getProperty("user"));
        }
        if(pro.getProperty("pw") != null && !"".equals(pro.getProperty("pw"))){
            ckProperties.setPassword(pro.getProperty("pw"));
        }

        String url = pro.getProperty("url");
        logger.info("开始创建clickhouse数据源连接,url:{}", url);
        ckProperties.setCompress(true);
//        ckProperties.setCompress(false);
//        ckProperties.setDecompress(false);
        config.setDataSource(new ClickHouseDataSource(url, ckProperties));
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(0); // 启动时不创建连接
        config.setPoolName("ClickHouseHikariCP");
        this.jdbcTemplate =  new JdbcTemplate(new HikariDataSource(config));
        logger.info("clickhouse数据源连接创建完毕,url:{}", url);
    }

    public QueryResultResp query(SqlTemplate sqlTemplate) throws Throwable {
        return ResultHandlerService.handler.mysqlHandler(jdbcTemplate.queryForList(sqlTemplate.getTemplateSql()));
    }

    public void exec(SqlTemplate sqlTemplate) throws Throwable {
        jdbcTemplate.execute(sqlTemplate.getTemplateSql());
    }

    public QueryResultResp queryCount(String countSql) throws SqlParseException, SQLException, JsonProcessingException, IOException {
        final QueryResultResp resp = new QueryResultResp();
        try {
            resp.setTotalSize(jdbcTemplate.queryForObject(countSql, Integer.class));
        } catch (Throwable e) {
            resp.setExceptionMsg(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    public void close() throws SQLException {

    }

    public void insert(DataCopyReq req, List<ColumnMetaResp> columnMetas, List<List<Object>> results) {
        if (CollectionUtils.isEmpty(results)) {
            logger.info("ClickHouseQueryService, results is empty, return.");
            return;
        }

        String insrSQLTemplate = "INSERT INTO %s (%s) VALUES (%s)";
        String columnNames = columnMetas.stream().map(ColumnMetaResp::getName).collect(Collectors.joining(","));
        String questionMarkStr = columnMetas.stream().map(item -> "?").collect(Collectors.joining(","));
        List<Object[]> values = results.stream().map(List::toArray).collect(Collectors.toList());

        String insertSQL = String.format(insrSQLTemplate, req.getDatabaseName() + "." + req.getTableName(), columnNames, questionMarkStr);

        jdbcTemplate.batchUpdate(insertSQL, values);
        logger.info("ClickHouseQueryService, {} Rows are inserted successfully.", results.size());
    }
}
