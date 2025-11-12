package com.ecoMarket.ServicioUsuarios.DTO;

import com.ecoMarket.ServicioUsuarios.Model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegistroConRolDTO {
    @Schema(example = "usuario", description = "Nombre de usuario registrado")
    private String username;
    @Schema(example = "contraseña", description = "Contraseña del usuario")
    private String password;

    @Schema(example = "correo", description = "correo del usuario")
    private String correo;

    @Schema(example = "ROL_TRABAJADOR", description = "roles: ROL_CLIENTE,ROL_TRABAJADOR,ROL_ADMIN")
    private Rol rol;
}
