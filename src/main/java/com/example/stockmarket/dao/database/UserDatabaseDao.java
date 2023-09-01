package com.example.stockmarket.dao.database;

import ch.qos.logback.core.net.HardenedObjectInputStream;
import com.example.stockmarket.controller.request.UserRequest;
import com.example.stockmarket.dao.UserMapper;
import com.example.stockmarket.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserDatabaseDao {
    private final JdbcTemplate jdbcTemplate;

    public User getUserByUsername(String username) throws UsernameNotFoundException {
        String getUserSql = "SELECT username, password, enabled FROM users WHERE username = ?";
        String getAuthoritiesSql = "SELECT authority FROM authorities WHERE username = ?";

        User user = jdbcTemplate.queryForObject(getUserSql, new UserMapper(), username);
        List<String> authorities = jdbcTemplate.queryForList(getAuthoritiesSql, String.class, username);
        Set<GrantedAuthority> authorities1 = authorities.stream()
                .map(i -> new SimpleGrantedAuthority(i))
                .collect(Collectors.toSet());
        user.setAuthorities(new HashSet<>(authorities1));
        return user;
    }
}
