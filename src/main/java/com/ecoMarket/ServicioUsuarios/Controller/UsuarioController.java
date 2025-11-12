package com.ecoMarket.ServicioUsuarios.Controller;

import com.ecoMarket.ServicioUsuarios.Model.Usuario;
import com.ecoMarket.ServicioUsuarios.Service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con las usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_TRABAJADOR')")
    @GetMapping
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Requiere rol ROL_ADMIN o ROL_TRABAJADOR, Lista todos los usuarios del sistema",
            security =@SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Usuarios listados exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema
                                    (schema = @Schema(implementation = Usuario.class))
                            )
            ),
            @ApiResponse(responseCode = "204", description = "No se encontraron Usuarios",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioService.findAll();
        if(usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }


    @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_TRABAJADOR')")
    @GetMapping("/{username}")
    @Operation(
            summary = "Obtener un usuario por su nombre de usuario",
            description = "Requiere rol ROL_ADMIN o ROL_TRABAJADOR",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Usuario> obtenerPorUsername(
            @Parameter(
                    name = "username",
                    description = "Nombre de usuario a buscar",
                    required = true,
                    example = "pedro.perez"
            )
            @PathVariable String username) {
        try {
            Usuario usuario = usuarioService.findByEmail(username);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PreAuthorize("hasAnyAuthority('ROL_ADMIN')")
    @DeleteMapping("/admin/{user}")
    @Operation(summary = "Eliminar usuario",description = "solo para ROL_ADMIN",security =@SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Usuario eliminado exitosamente",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(responseCode = "404",
                    description = "No se encontro el Usuario",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    public ResponseEntity<?> eliminar(
            @Parameter(
                    name = "user",
                    description = "Nombre de usuario",
                    required = true,
                    example = "pedro.perez",
                    schema = @Schema(type = "String")
            )
            @PathVariable("user") String user){
        try{
            usuarioService.deleteByUsername(user);
            return ResponseEntity.noContent().build();
        }catch( Exception e ) {
            return ResponseEntity.notFound().build();
        }
    }
}
