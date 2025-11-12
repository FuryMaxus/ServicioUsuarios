package com.ecoMarket.ServicioUsuarios;

import com.ecoMarket.ServicioUsuarios.Model.Usuario;
import com.ecoMarket.ServicioUsuarios.Repository.UsuarioRepository;
import com.ecoMarket.ServicioUsuarios.Service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.ecoMarket.ServicioUsuarios.Model.Rol.ROL_CLIENTE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    public void testSave() {
        Usuario usuario = new Usuario(1L,"usuario","passwd","correo@direccion.com" ,ROL_CLIENTE);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        Usuario saved = usuarioService.save(usuario);

        assertNotNull(saved);
        assertEquals("usuario",saved.getName());
    }

    @Test
    public void testLoadUserByUsername_UsuarioExiste() {
        Usuario usuario = new Usuario();
        usuario.setName("usuario123");
        usuario.setPassword("secreta");
        usuario.setRol(ROL_CLIENTE);

        when(usuarioRepository.findByEmail("usuario123"))
                .thenReturn(Optional.of(usuario));


        UserDetails result = usuarioService.loadUserByUsername("usuario123");


        assertNotNull(result);
        assertEquals("usuario123", result.getUsername());
        assertEquals("secreta", result.getPassword());
        assertIterableEquals(List.of(new SimpleGrantedAuthority("ROL_CLIENTE")), result.getAuthorities());
    }

    @Test
    public void testLoadUserByUsername_UsuarioNoExiste() {
        when(usuarioRepository.findByEmail("noexiste"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            usuarioService.loadUserByUsername("noexiste");
        });
    }

    @Test
    public void testExistByUsernametest() {
        String username = "usuario123";
        Usuario usuario = new Usuario();
        usuario.setName(username);

        when(usuarioRepository.findByEmail(username)).thenReturn(Optional.of(usuario));

        boolean exists = usuarioService.existsByUsername(username);

        assertTrue(exists);
    }


}
