package com.ecoMarket.ServicioUsuarios.Service;
import com.ecoMarket.ServicioUsuarios.Model.Usuario;
import com.ecoMarket.ServicioUsuarios.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<Usuario> findAll(){ return usuarioRepository.findAll(); }

    public Usuario findByEmail(String email){
        return usuarioRepository.findByEmail(email).get();
    }

    public void deleteByUsername(String user){usuarioRepository.deleteByEmail(user);}

    public Usuario save(Usuario usuario){
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public boolean existsByUsername(String username){
        return usuarioRepository.findByEmail(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        System.out.println("🔍 Login: Buscando usuario con email: [" + email + "]");

        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {

                    System.out.println("❌ Login: Usuario NO ENCONTRADO en la BD.");
                    return new UsernameNotFoundException("Usuario no encontrado");
                });


        System.out.println("✅ Login: Usuario ENCONTRADO. Hash en BD: " + user.getPassword());


        System.out.println("ℹ️ Rol del usuario: " + user.getRol());

        return new User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRol().name())));
    }
}
