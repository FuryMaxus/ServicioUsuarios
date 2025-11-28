package com.ecoMarket.ServicioUsuarios;

import com.ecoMarket.ServicioUsuarios.Model.Rol;
import com.ecoMarket.ServicioUsuarios.Model.Usuario;
import com.ecoMarket.ServicioUsuarios.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner crearAdminPorDefecto(UsuarioRepository usuarioRepository,
                                                  PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByEmail("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setName("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@levelUpGamer.cl");
                admin.setRol(Rol.ROL_ADMIN);

                usuarioRepository.save(admin);
                System.out.println("Usuario admin creado: admin@levelUpGamer.cl / admin123");
            } else {
                System.out.println("Admin ya existe, no se creo otro.");
            }
        };
    }
}
