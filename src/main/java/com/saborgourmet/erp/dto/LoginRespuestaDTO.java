package com.saborgourmet.erp.dto;

public record LoginRespuestaDTO(
        String token,
        String tipo,
        UsuarioRespuestaDTO usuario
) {
}
