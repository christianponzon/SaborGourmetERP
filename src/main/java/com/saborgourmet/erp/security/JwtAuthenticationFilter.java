package com.saborgourmet.erp.security;

import com.saborgourmet.erp.models.Usuario;
import com.saborgourmet.erp.repositories.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository) {

        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization =
                request.getHeader("Authorization");

        // Si no hay token, continuar.
        // Spring Security protegera las rutas privadas.
        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7).trim();

        // Validar token JWT
        try {

            if (token.isEmpty() ||
                    !jwtService.validarToken(token)) {

                responderNoAutorizado(
                    response,
                    "Token JWT invalido o expirado"
                );
                return;
            }

            String correo = jwtService.obtenerCorreo(token);

            Optional<Usuario> usuarioEncontrado =
                    usuarioRepository.buscarPorCorreo(correo);

            // Verificar que el usuario exista
            if (usuarioEncontrado.isEmpty()) {

                responderNoAutorizado(
                    response,
                    "El usuario del token no existe"
                );
                return;
            }

            Usuario usuario = usuarioEncontrado.get();

            // Verificar que el usuario este activo
            if (!usuario.isActivo()) {

                responderNoAutorizado(
                    response,
                    "El usuario esta desactivado"
                );
                return;
            }

            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                SimpleGrantedAuthority autoridad =
                        new SimpleGrantedAuthority(
                            "ROLE_" + usuario.getRol()
                        );

                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(
                            usuario.getCorreo(),
                            null,
                            List.of(autoridad)
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(autenticacion);
            }

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            responderNoAutorizado(
                response,
                "Token JWT invalido o expirado"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Respuesta para errores de autenticacion
    private void responderNoAutorizado(
            HttpServletResponse response,
            String mensaje) throws IOException {

        SecurityContextHolder.clearContext();

        response.setStatus(
            HttpStatus.UNAUTHORIZED.value()
        );

        response.setContentType(
            MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
            "{\"error\":\"No autorizado\","
            + "\"mensaje\":\"" + mensaje + "\"}"
        );
    }
}
