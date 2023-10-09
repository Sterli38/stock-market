package com.example.stockmarket.dao.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlBuilder {
    private final StringBuilder sql = new StringBuilder(); // sql запрос
    private final List<String> clauses = new ArrayList<>(); // Список из строк с условиями
    private final Map<String, Object> valuesSql = new HashMap<>();

    String state = "null";

    public final Map<String, List> map = new HashMap<>();

    public void init() {
        map.put("null", Collections.singletonList("SELECT"));
        map.put("SELECT", Collections.singletonList("FROM"));
        map.put("FROM", Collections.singletonList("JOIN"));
        map.put("JOIN", Collections.singletonList("ON"));
        List<String> onValues = new ArrayList<>();
        onValues.add("JOIN");
        onValues.add("WHERE");
        onValues.add("GROUP BY");

        map.put("ON", onValues);
        List<String> whereList = new ArrayList<>();
        whereList.add("GROUP BY");
        whereList.add("WHERE");
        map.put("WHERE", whereList);
    }


    public SqlBuilder select2(String rows) {
        init();
        if (checkTransition(state, "SELECT")) {
            sql.append("SELECT ");
            sql.append(rows);
            state = "SELECT";
        } else {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        return this;
    }

    public SqlBuilder from2(String table) {
        if (checkTransition(state, "FROM")) {
            sql.append(" FROM ");
            sql.append(table);
            state = "FROM";
        } else {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        return this;

    }

    public SqlBuilder where2(String clause) {
        if (checkTransition(state, "WHERE")) {
            clauses.add(clause);
            state = "WHERE";
        } else {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        return this;

    }

    public SqlBuilder join(String table) {
        if (checkTransition(state, "JOIN")) {
            sql.append(" JOIN ");
            sql.append(table);
            state = "JOIN";
        } else {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        return this;
    }

    public SqlBuilder on(String condition) {
        if (checkTransition(state, "ON")) {
            sql.append(" ON ");
            sql.append(condition);
            state = "ON";
        } else {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        return this;
    }

    public SqlBuilder groupBy(String condition) {
        if (checkTransition(state, "GROUP BY")) {
            sql.append(" GROUP BY ");
            sql.append(condition);
            state = "GROUP BY";
        } else {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        return this;

    }

    private boolean checkTransition(String from, String to) {
        if (map.containsKey(from)) {
            List valuesList = map.get(from);
            if (valuesList.contains(to)) {
                return true;
            }
        }
        return false;
    }

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
        if (!clauses.isEmpty()) {
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
                    sql.append(clauses.get(i));
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
        sql.append(" as ").append(alias);
        return this;
    }

    public Map<String, Object> getMap() {
        return valuesSql;
    }

    public String getSql() {
        return sql.toString();
    }
}