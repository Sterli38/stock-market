package com.example.stockmarket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class test {
    public test(JdbcTemplate jdbcTemplate) {
//         log.info(String.valueOf(jdbcTemplate.queryForObject("SELECT count(*) FROM participant", Integer.class)));
    }
}
