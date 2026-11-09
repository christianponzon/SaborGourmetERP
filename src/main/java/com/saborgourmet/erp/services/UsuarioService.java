package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Usuario;
import com.saborgourmet.erp.repositories.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar un usuario
    public synchronized Usuario registrar(Usuario usuario) {

        if (usuario == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Los datos del usuario son obligatorios"
            );
        }

        if (usuario.getNombre() == null ||
            usuario.getNombre().isBlank() ||
            usuario.getApellido() == null ||
            usuario.getApellido().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El nombre y apellido son obligatorios"
            );
        }

        if (usuario.getCorreo() == null ||
            !usuario.getCorreo().matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ingrese un correo electrónico válido"
            );
        }

        String correo = usuario.getCorreo()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existeCorreo(correo)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El correo electrónico ya está registrado"
            );
        }

        if (usuario.getPassword() == null ||
            usuario.getPassword().length() < 8) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La contraseña debe tener al menos 8 caracteres"
            );
        }

        // Proteger la contraseña
        String passwordHash = passwordEncoder.encode(
            usuario.getPassword()
        );

        usuario.setPassword(passwordHash);

        // Datos controlados por el servidor
        usuario.setId(null);
        usuario.setRol("CLIENTE");
        usuario.setVerificado(false);
        usuario.setActivo(true);
        usuario.setCorreo(correo);

        return usuarioRepository.guardar(usuario);
    }

    // Buscar usuario por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id);
    }

    // Buscar usuario por correo
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.buscarPorCorreo(correo);
    }

    // Listar usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }

    // Comprobar contraseña durante el login
    public boolean verificarPassword(
            String passwordIngresada,
            String passwordGuardada) {

        if (passwordIngresada == null ||
            passwordGuardada == null) {
            return false;
        }

        return passwordEncoder.matches(
            passwordIngresada,
            passwordGuardada
        );
    }


    // Iniciar sesión
    public Usuario iniciarSesion(String correo, String password) {

        if (correo == null || correo.isBlank() ||
            password == null || password.isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El correo y la contraseña son obligatorios"
            );
        }

        String correoNormalizado = correo.trim()
                .toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository
                .buscarPorCorreo(correoNormalizado)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos"
                ));

        if (!verificarPassword(password, usuario.getPassword())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Correo o contraseña incorrectos"
            );
        }

        if (!usuario.isActivo()) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "La cuenta está desactivada"
            );
        }

        return usuario;
    }
}
