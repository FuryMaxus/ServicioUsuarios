package com.ecoMarket.ServicioUsuarios.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegistroDTO {
    @Schema(example = "usuario", description = "Nombre de usuario registrado")
    private String username;
    @Schema(example = "contraseña", description = "Contraseña del usuario")
    private String password;
    @Schema(example = "correo", description = "correo del usuario")
    private String email;
    @Schema(example = "direccion", description = "direccion del usuario")
    private String address;
}
