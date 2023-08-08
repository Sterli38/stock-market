package com.example.stockmarket.dao.database;

import java.util.ArrayList;
import java.util.List;

public class SqlBuilder {
    private final StringBuilder sql = new StringBuilder(); // sql запрос
    private final List<String> clauses = new ArrayList<>(); // Список из строк с условиями

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

    public String getSQL() {
        return sql.toString();
    }
}