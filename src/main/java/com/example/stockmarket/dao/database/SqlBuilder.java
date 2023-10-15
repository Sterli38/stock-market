package com.example.stockmarket.dao.database;

import com.example.stockmarket.entity.SqlState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlBuilder {
    private final StringBuilder sql = new StringBuilder(); // sql запрос
    private final List<String> clauses = new ArrayList<>(); // Список из строк с условиями
    private final Map<String, Object> valuesSql = new HashMap<>();
    private SqlState state = SqlState.NONE;
    public final Map<SqlState, List<SqlState>> map;

    {
        map = new HashMap<>() {{
            put(SqlState.NONE, Collections.singletonList(SqlState.SELECT));
            put(SqlState.SELECT, Collections.singletonList(SqlState.FROM));
            put(SqlState.FROM, List.of(SqlState.JOIN, SqlState.WHERE));
            put(SqlState.JOIN, Collections.singletonList(SqlState.ON));
            put(SqlState.ON, List.of(SqlState.JOIN, SqlState.WHERE, SqlState.GROUP_BY));
            put(SqlState.WHERE, List.of(SqlState.GROUP_BY, SqlState.WHERE));
        }};
    }

    public SqlBuilder select2(String rows) {
        if (!isTransitionCorrect(state, SqlState.SELECT)) {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        sql.append("SELECT ").append(rows);
        state = SqlState.SELECT;
        return this;
    }

    public SqlBuilder from2(String table) {
        if (!isTransitionCorrect(state, SqlState.FROM)) {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        sql.append(" FROM ").append(table);
        state = SqlState.FROM;
        return this;
    }

    public SqlBuilder where2(String clause) {
        if (!isTransitionCorrect(state, SqlState.WHERE)) {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        clauses.add(clause);
        state = SqlState.WHERE;
        return this;
    }

    public SqlBuilder join(String table) {
        if (!isTransitionCorrect(state, SqlState.JOIN)) {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        sql.append(" JOIN ").append(table);
        state = SqlState.JOIN;
        return this;
    }

    public SqlBuilder on(String condition) {
        if (!isTransitionCorrect(state, SqlState.ON)) {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        sql.append(" ON ").append(condition);
        state = SqlState.ON;
        return this;
    }

    public SqlBuilder groupBy(String condition) {
        if (!isTransitionCorrect(state, SqlState.GROUP_BY)) {
            throw new IllegalArgumentException("Неверный порядок запроса");
        }
        sql.append(" GROUP BY ").append(condition);
        state = SqlState.GROUP_BY;
        return this;

    }

    private boolean isTransitionCorrect(SqlState from, SqlState to) {
        List<SqlState> possibleTransitions = map.get(from);
        if (possibleTransitions != null) {
            return possibleTransitions.contains(to);
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
        if (clauses.isEmpty()) {
            return;
        }
        sql.append(" WHERE ");
        for (int i = 0; i < clauses.size(); i++) {
            if (i != clauses.size() - 1) {
                sql.append(clauses.get(i)).append(" and ");
            } else {
                sql.append(clauses.get(i)).append(" ");
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