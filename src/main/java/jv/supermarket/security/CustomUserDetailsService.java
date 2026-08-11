package jv.supermarket.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jv.supermarket.user.Usuario;
import jv.supermarket.user.UsuarioService;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioService service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = service.getByEmail(username);

        return new User(user.getEmail(), user.getSenha(), user.getRoles());
    }
    
}
