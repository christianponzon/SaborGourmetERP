package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.dto.LoginDTO;
import com.saborgourmet.erp.dto.LoginRespuestaDTO;
import com.saborgourmet.erp.dto.RegistroUsuarioDTO;
import com.saborgourmet.erp.dto.UsuarioRespuestaDTO;

import com.saborgourmet.erp.models.Usuario;
import com.saborgourmet.erp.services.UsuarioService;
import com.saborgourmet.erp.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService) {

        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    // ==========================================
    // REGISTRAR USUARIO
    // ==========================================
    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuestaDTO> registrar(
            @Valid @RequestBody RegistroUsuarioDTO datos) {

        Usuario usuario = new Usuario();

        usuario.setNombre(datos.nombre());
        usuario.setApellido(datos.apellido());
        usuario.setCorreo(datos.correo());
        usuario.setPassword(datos.password());
        usuario.setTelefono(datos.telefono());

        Usuario registrado = usuarioService.registrar(usuario);

        UsuarioRespuestaDTO respuesta =
                UsuarioRespuestaDTO.desdeUsuario(registrado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    // ==========================================
    // INICIAR SESION CON JWT
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<LoginRespuestaDTO> login(
            @Valid @RequestBody LoginDTO datos) {

        // 1. Verificar correo y contraseña
        Usuario usuario = usuarioService.iniciarSesion(
                datos.correo(),
                datos.password()
        );

        // 2. Generar token JWT
        String token = jwtService.generarToken(
                usuario.getCorreo(),
                usuario.getRol()
        );

        // 3. Preparar datos públicos del usuario
        UsuarioRespuestaDTO usuarioRespuesta =
                UsuarioRespuestaDTO.desdeUsuario(usuario);

        // 4. Construir respuesta
        LoginRespuestaDTO respuesta =
                new LoginRespuestaDTO(
                        token,
                        "Bearer",
                        usuarioRespuesta
                );

        // 5. Devolver token y usuario
        return ResponseEntity.ok(respuesta);
    }
}
