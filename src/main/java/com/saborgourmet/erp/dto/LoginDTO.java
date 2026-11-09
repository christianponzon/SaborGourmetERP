package com.saborgourmet.erp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo electrónico válido")
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    String password

) {
}
