package com.gerenciamento.cursos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança do Spring Security.
 * Define regras de autorização e autenticação.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                // Permite acesso público à página de login e recursos estáticos
                .requestMatchers("/login.html", "/css/**", "/js/login.js", "/api/auth/login").permitAll()
                // Console H2 acessível sem autenticação (apenas para desenvolvimento)
                .requestMatchers("/h2-console/**").permitAll()
                // Endpoints de API requerem autenticação
                .requestMatchers("/api/**").authenticated()
                // Página principal requer autenticação
                .requestMatchers("/", "/index.html", "/js/app.js").authenticated()
                // Qualquer outra requisição requer autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/api/auth/login")
                .defaultSuccessUrl("/index.html", true)
                .failureUrl("/login.html?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login.html?logout=true")
                .permitAll()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // Para H2 Console

        return http.build();
    }
}
