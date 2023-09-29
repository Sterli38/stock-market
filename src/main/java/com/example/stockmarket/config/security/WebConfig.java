package com.example.stockmarket.config.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class WebConfig {
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @ConditionalOnProperty(name = "security.enabled", havingValue = "true")
    @Bean
    public SecurityFilterChain configureWithSecurity(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(i -> {
                    i.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                            .requestMatchers("/participant/edit").permitAll()
                            .requestMatchers("/login").permitAll()
                            .requestMatchers("/swagger-u/**").permitAll()
                            .anyRequest().authenticated();
                });

        return http.build();
    }

    @ConditionalOnProperty(name = "security.enabled", havingValue = "false")
    @Bean
    public SecurityFilterChain configureWithoutSecurity(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(i -> {
                    i.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                            .requestMatchers("/**").permitAll();
                });

        return http.build();
    }
}
