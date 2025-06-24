package com.proyectos.organizacion_eventos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return org.springframework.security.core.userdetails.User 
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles().stream()
                .map(roles -> roles.getName()).toArray(String[]::new))
            .build();
    }

}
