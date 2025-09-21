package com.example.clickhouse.entry.req;

public class DataCopyReq {
    private String databaseName;
    private String tableName;

    public DataCopyReq(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
