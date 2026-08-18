package jv.supermarket.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jv.supermarket.user.User;
import jv.supermarket.user.UserService;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByEmail(username);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), user.getRoles());
    }

}
