package com.saborgourmet.erp.repositories;

import com.saborgourmet.erp.models.Usuario;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UsuarioRepository {

    // Almacenamiento temporal en memoria
    private final ConcurrentHashMap<Long, Usuario> usuarios =
            new ConcurrentHashMap<>();

    // Generador de identificadores
    private final AtomicLong contador = new AtomicLong(0);

    // Guardar un usuario
    public synchronized Usuario guardar(Usuario usuario) {

        if (usuario.getId() == null) {
            usuario.setId(contador.incrementAndGet());
        }

        usuarios.put(usuario.getId(), usuario);

        return usuario;
    }

    // Buscar usuario por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    // Buscar usuario por correo
    public Optional<Usuario> buscarPorCorreo(String correo) {

        if (correo == null) {
            return Optional.empty();
        }

        return usuarios.values()
                .stream()
                .filter(usuario ->
                        usuario.getCorreo() != null &&
                        usuario.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }

    // Verificar si un correo está registrado
    public boolean existeCorreo(String correo) {
        return buscarPorCorreo(correo).isPresent();
    }

    // Eliminar un usuario
    public boolean eliminar(Long id) {
        return usuarios.remove(id) != null;
    }
}
