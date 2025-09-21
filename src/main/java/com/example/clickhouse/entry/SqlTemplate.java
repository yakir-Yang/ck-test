package com.example.clickhouse.entry;

public class SqlTemplate {
    private String templateSql;

    public SqlTemplate(String templateSql) {
        this.templateSql = templateSql;
    }

    public String getTemplateSql() {
        return templateSql;
    }

    public void setTemplateSql(String templateSql) {
        this.templateSql = templateSql;
    }
}
