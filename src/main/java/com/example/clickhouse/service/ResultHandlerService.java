package com.example.clickhouse.service;

import com.example.clickhouse.entry.resp.QueryResultResp;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class ResultHandlerService {
    public static final ResultHandlerService handler = new ResultHandlerService();

    public QueryResultResp mysqlHandler(List<Map<String, Object>> queryResult) {
        QueryResultResp resp = new QueryResultResp();
        resp.setData(queryResult);
        resp.setTotalSize(queryResult.size());
        return resp;
    }
}
