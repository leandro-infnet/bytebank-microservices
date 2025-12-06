package br.com.bytebank.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // TODO: configurar as permissões corretamente
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth.anyRequest().permitAll())
                .headers(
                        headers -> headers.frameOptions(
                                frameOptions -> frameOptions.sameOrigin()))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Define o BCrypt como o algoritmo de hashing de senhas
        return new BCryptPasswordEncoder();
    }
}