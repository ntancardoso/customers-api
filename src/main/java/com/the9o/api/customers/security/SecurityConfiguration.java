package com.the9o.api.customers.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${api.user:test}")
    private String apiUser;

    @Value("${api.pass:test}")
    private String apiPass;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/customers").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/customers/**").hasRole("ADMIN")
                .anyRequest().authenticated() // Ensure other requests are authenticated
            )
            .csrf().disable(); // Consider enabling CSRF protection in production
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .inMemoryAuthentication()
            .withUser(apiUser)
            .password(passwordEncoder().encode(apiPass))
            .roles("USER", "ADMIN");
        return authenticationManagerBuilder.build();
    }
}
