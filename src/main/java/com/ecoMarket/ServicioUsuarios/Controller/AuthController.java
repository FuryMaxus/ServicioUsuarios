package com.ecoMarket.ServicioUsuarios.Controller;

import com.ecoMarket.ServicioUsuarios.DTO.AuthRequestDTO;
import com.ecoMarket.ServicioUsuarios.DTO.RegistroDTO;
import com.ecoMarket.ServicioUsuarios.DTO.RegistroConRolDTO;
import com.ecoMarket.ServicioUsuarios.DTO.AuthResponseDTO;
import com.ecoMarket.ServicioUsuarios.Model.Rol;
import com.ecoMarket.ServicioUsuarios.Model.Usuario;
import com.ecoMarket.ServicioUsuarios.Jwt.JwtUtil;
import com.ecoMarket.ServicioUsuarios.Service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticacion",description = "Operaciones relacionadas con registros/inicios de sesion")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registro")
    @Operation(
            summary = "Registro de clientes",
            description = "registro de usuarios de tipo ROL_CLIENTE, no se necesita ningun rol especifico para usarse")
    public ResponseEntity<?> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos cliente a registrar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthRequestDTO.class)
                    )
            )
            @RequestBody RegistroDTO registro){
        Usuario usuario = new Usuario();
        usuario.setName(registro.getUsername());
        usuario.setPassword(registro.getPassword());
        usuario.setEmail(registro.getEmail());
        usuario.setRol(Rol.ROL_CLIENTE);
        if (registro.getAddress() != null && !registro.getAddress().isEmpty()) {
            usuario.setAddress(registro.getAddress());
        }
        Usuario user = usuarioService.save(usuario);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @PostMapping("/registro/admin")
    @Operation(
            summary = "Registro de usuarios",
            description = "registro de usuarios de tipo ROL_CLIENTE, ROL_TRABAJADOR O ROL_ADMIN, se necesita ROL_ADMIN para usar",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<?> registrarConRol(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegistroConRolDTO.class)
                    )
            )
            @RequestBody RegistroConRolDTO registro) {
        if (usuarioService.existsByUsername(registro.getUsername())) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setName(registro.getUsername());
        usuario.setPassword(registro.getPassword());
        usuario.setEmail(registro.getCorreo());
        usuario.setRol(registro.getRol());
        Usuario saved = usuarioService.save(usuario);
        return ResponseEntity.ok("Usuario creado con rol: " + saved.getRol());
    }

    @PostMapping("/ingreso")
    @Operation(
            summary = "Inicio de sesion",
            description = "Devuelve un token JWT que debe usarse en los siguientes endpoints protegidos en el header `Authorization` como `Bearer <token>` "
    )
    public ResponseEntity<?> ingresar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales para iniciar sesion",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthRequestDTO.class)
                    )
            )
            @RequestBody AuthRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        final UserDetails userDetails = usuarioService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponseDTO(jwt));
    }


}

