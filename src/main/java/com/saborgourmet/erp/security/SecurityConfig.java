package com.saborgourmet.erp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // API REST sin sesiones
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Desactivar autenticacion tradicional
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // Manejo de errores de seguridad
            .exceptionHandling(exception -> exception

                // Usuario no autenticado
                .authenticationEntryPoint(
                    (request, response, authException) -> {

                        response.setStatus(
                            HttpStatus.UNAUTHORIZED.value()
                        );

                        response.setContentType(
                            MediaType.APPLICATION_JSON_VALUE
                        );

                        response.setCharacterEncoding("UTF-8");

                        response.getWriter().write(
                            "{\"error\":\"No autorizado\","
                            + "\"mensaje\":\"Debes iniciar sesion "
                            + "con un token JWT valido\"}"
                        );
                    }
                )

                // Usuario autenticado sin permisos
                .accessDeniedHandler(
                    (request, response, accessDeniedException) -> {

                        response.setStatus(
                            HttpStatus.FORBIDDEN.value()
                        );

                        response.setContentType(
                            MediaType.APPLICATION_JSON_VALUE
                        );

                        response.setCharacterEncoding("UTF-8");

                        response.getWriter().write(
                            "{\"error\":\"Acceso denegado\","
                            + "\"mensaje\":\"No tienes permisos "
                            + "para realizar esta operacion\"}"
                        );
                    }
                )
            )

            // Configurar permisos
            .authorizeHttpRequests(auth -> auth

                // Pagina principal publica
                .requestMatchers(
                    HttpMethod.GET,
                    "/"
                ).permitAll()

                // Registro y login publicos
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/registro",
                    "/api/auth/login"
                ).permitAll()

                // Manejo de errores
                .requestMatchers("/error").permitAll()

                // Las demas rutas requieren autenticacion
                .anyRequest().authenticated()
            )

            // Validar tokens JWT
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
