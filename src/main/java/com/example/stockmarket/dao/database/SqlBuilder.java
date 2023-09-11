package com.example.stockmarket.dao.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlBuilder {
    private final StringBuilder sql = new StringBuilder(); // sql запрос
    private final List<String> clauses = new ArrayList<>(); // Список из строк с условиями
    private final Map<String, Object> valuesSql = new HashMap<>();

    public SqlBuilder select(String rows) { // Что выбрать
        sql.append("SELECT ");
        sql.append(rows);
        return this;
    }

    public SqlBuilder from(String tables) { // Откуда выбираем
        sql.append(" FROM ");
        sql.append(tables);
        return this;
    }

    public SqlBuilder where(String clause) { // Добавление условия выборки
        clauses.add(clause);
        return this;
    }

    public SqlBuilder condition(String condition, String clause) {
        sql.append(condition);
        sql.append(clause);
        return this;
    }

    public void build() { // Строим запрос
        if (clauses.size() > 0) {
            sql.append(" WHERE ");
            for (int i = 0; i < clauses.size(); i++) {
                if (i != clauses.size() - 1) {
                    sql.append(clauses.get(i)).append(" and ");
                } else {
                    sql.append(clauses.get(i)).append(" ");
                }
            }
        }
    }

    public SqlBuilder buildCondition(List<String> conditions, List<String> conditionsValue, List<Object> values) {
        if (!values.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) != null) {
                    this.where(conditions.get(i));
                    valuesSql.put(conditionsValue.get(i), values.get(i));
                }
            }
        }
        if (!clauses.isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < clauses.size(); i++) {
                if (i != clauses.size() - 1) {
                    sql.append(clauses.get(i)).append(" and ");
                } else {
                    sql.append(clauses.get(i)).append("");
                }
            }
        }
        return this;
    }

    public String union(String sql1, String sql2) {
        return sql1 + (" UNION ") + sql2;
    }

    public String buildSubQuery(String subQuery) {
        return "(" + subQuery + ")";
    }

    public SqlBuilder addAlias(String alias) {
        sql.append(" as " + alias);
        return this;
    }

    public Map<String, Object> getMap() {
        return valuesSql;
    }

    public String getSql() {
        return sql.toString();
    }
}