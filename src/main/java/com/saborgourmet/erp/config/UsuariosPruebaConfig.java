package com.saborgourmet.erp.config;

import com.saborgourmet.erp.models.Usuario;
import com.saborgourmet.erp.repositories.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class UsuariosPruebaConfig {

    @Bean
    public CommandLineRunner cargarUsuariosPrueba(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            crearUsuario(
                usuarioRepository,
                passwordEncoder,
                "Ana",
                "Torres",
                "gerente@demo.local",
                "GERENTE"
            );

            crearUsuario(
                usuarioRepository,
                passwordEncoder,
                "Luis",
                "Ramos",
                "mesero@demo.local",
                "MESERO"
            );

            crearUsuario(
                usuarioRepository,
                passwordEncoder,
                "Pedro",
                "Flores",
                "cocinero@demo.local",
                "COCINERO"
            );

            crearUsuario(
                usuarioRepository,
                passwordEncoder,
                "Maria",
                "Lopez",
                "cajero@demo.local",
                "CAJERO"
            );

            crearUsuario(
                usuarioRepository,
                passwordEncoder,
                "Jose",
                "Diaz",
                "repartidor@demo.local",
                "REPARTIDOR"
            );

            crearUsuario(
                usuarioRepository,
                passwordEncoder,
                "Carlos",
                "Mendoza",
                "cliente@demo.local",
                "CLIENTE"
            );

            System.out.println(
                "Usuarios de prueba cargados correctamente"
            );
        };
    }

    private void crearUsuario(
            UsuarioRepository repositorio,
            PasswordEncoder passwordEncoder,
            String nombre,
            String apellido,
            String correo,
            String rol) {

        if (repositorio.existeCorreo(correo)) {
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);

        // Contraseña temporal solo para desarrollo
        usuario.setPassword(
            passwordEncoder.encode("Demo12345!")
        );

        usuario.setTelefono("999999999");
        usuario.setRol(rol);
        usuario.setVerificado(true);
        usuario.setActivo(true);

        repositorio.guardar(usuario);
    }
}
