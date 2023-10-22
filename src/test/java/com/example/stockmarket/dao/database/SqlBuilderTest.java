package com.example.stockmarket.dao.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
public class SqlBuilderTest {

    @Test
    public void expectedExceptionByInitialStateTest() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sqlBuilder.groupBy("");
        });

        String expectedMessage = "Неверный порядок запроса";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void expectedExceptionBySelectTest() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sqlBuilder.select("table")
                    .join("mistake");
        });

        String expectedMessage = "Неверный порядок запроса";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test()
    public void expectedExceptionByFrom() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sqlBuilder.select("participant.id")
                    .from("participant")
                    .on("");
        });

        String expectedMessage = "Неверный порядок запроса";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void expectedExceptionByJoinTest() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sqlBuilder.select("TestRows")
                    .from("testTable")
                    .join("testTable2").on("condition")
                    .from("mistakeTable");
        });

        String expectedMessage = "Неверный порядок запроса";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void expectedExceptionByWhereTest() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sqlBuilder.select("rows")
                    .from("table")
                    .where("clauses")
                    .on("mistake");
        });

        String expectedMessage = "Неверный порядок запроса";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void validQueryTest() {
        SqlBuilder sqlBuilder = new SqlBuilder();

        String actualQuery = sqlBuilder.select("rows")
                .from("table")
                .join("joinTable").on("condition")
                .where("whereClause")
                .where("whereClause")
                .groupBy("conditionForGrouping").build();

        String expectedQuery = "SELECT rows FROM table JOIN joinTable ON condition WHERE whereClause and whereClause GROUP BY conditionForGrouping";
        Assertions.assertEquals(expectedQuery, actualQuery);
    }
}
