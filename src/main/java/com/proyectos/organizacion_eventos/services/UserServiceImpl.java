package com.proyectos.organizacion_eventos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectos.organizacion_eventos.entities.Role;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.repositories.RoleRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Transactional
    @Override
    public User save(User user) {

        // Creamos la lista de roles que vendra con el usuario
        List<Role> roles = new ArrayList<>();

        Optional<Role> roleOptionalUser = roleRepository.findByName("ROLE_USER");
        roleOptionalUser.ifPresent(roles::add); // Agregamos el rol de usuario por defecto

        // Si el usuario es administrador, agregamos el rol de administrador
        if (user.isAdmin()) {
            Optional<Role> roleOptionalAdmin = roleRepository.findByName("ROLE_ADMIN");
            roleOptionalAdmin.ifPresent(roles::add); // Agregamos el rol de administrador
        }

        user.setRoles(roles); // Asignamos los roles al usuario

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword); // Encriptamos la contraseña

        return repository.save(user); // Guardamos el usuario en la base de datos
    }
}
