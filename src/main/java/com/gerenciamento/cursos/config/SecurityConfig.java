package com.gerenciamento.cursos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
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
                .requestMatchers("/login.html", "/css/**", "/js/login.js", "/api/auth/login", "/api/auth/me").permitAll()
                // Console H2 acessível sem autenticação (apenas para desenvolvimento)
                .requestMatchers("/h2-console/**").permitAll()
                
                // Dashboard: GET permitido para todos os perfis (somente leitura)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/alunos", "/api/usuarios/professores", "/api/cursos", "/api/cursos/disponiveis").authenticated()
                
                // ADMIN: Acesso total a usuários
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                
                // Alunos: ADMIN e PROFESSOR podem criar/editar/excluir
                .requestMatchers("/api/alunos/**").hasAnyRole("ADMIN", "PROFESSOR")
                
                // Cursos: ADMIN e PROFESSOR podem criar/editar/excluir
                .requestMatchers("/api/cursos/**").hasAnyRole("ADMIN", "PROFESSOR")
                
                // Matrículas: todos podem ver, mas controle específico no service
                .requestMatchers("/api/matriculas/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                
                // Página principal requer autenticação
                .requestMatchers("/", "/index.html", "/js/app.js", "/css/**").authenticated()
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
