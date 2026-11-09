package com.saborgourmet.erp.dto;

import com.saborgourmet.erp.models.Usuario;

public record UsuarioRespuestaDTO(

    Long id,
    String nombre,
    String apellido,
    String correo,
    String telefono,
    String rol,
    boolean verificado,
    boolean activo

) {

    public static UsuarioRespuestaDTO desdeUsuario(Usuario usuario) {

        return new UsuarioRespuestaDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getCorreo(),
            usuario.getTelefono(),
            usuario.getRol(),
            usuario.isVerificado(),
            usuario.isActivo()
        );
    }
}
