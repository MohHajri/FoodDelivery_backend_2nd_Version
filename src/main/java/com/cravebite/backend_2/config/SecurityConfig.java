package com.cravebite.backend_2.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cravebite.backend_2.security.JwtAuthenticationEntryPoint;
import com.cravebite.backend_2.security.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private JwtRequestFilter jwtRequestFilter;

        @Autowired
        private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                                                .requestMatchers("/test/**").permitAll()
                                                .requestMatchers("/auth/**").permitAll()
                                                // .requestMatchers("/user/**").permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                                .sessionManagement(
                                                sessionManagement -> sessionManagement
                                                                .sessionCreationPolicy(
                                                                                SessionCreationPolicy.STATELESS));
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

}