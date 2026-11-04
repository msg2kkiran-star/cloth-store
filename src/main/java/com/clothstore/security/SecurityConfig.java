package com.clothstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Customer registration is public
                        .requestMatchers(HttpMethod.POST, "/api/customers")
                        .permitAll()

                        // Product browsing is public
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .permitAll()

                        // Product modifications are ADMIN only
                        .requestMatchers("/api/products/**")
                        .hasRole("ADMIN")

                        // Customer cart, order, payment and address APIs
                        .requestMatchers(
                                "/api/customers/*/cart/**",
                                "/api/customers/*/orders/**",
                                "/api/customers/*/addresses/**"
                        )
                        .hasAnyRole("ADMIN", "CUSTOMER")

                        // Customer administration is ADMIN only
                        .requestMatchers("/api/customers/**")
                        .hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}