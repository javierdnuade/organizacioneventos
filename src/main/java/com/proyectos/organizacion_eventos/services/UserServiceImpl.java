package com.proyectos.organizacion_eventos.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectos.organizacion_eventos.dto.UserDTO;
import com.proyectos.organizacion_eventos.dto.UserUpdateDTO;
import com.proyectos.organizacion_eventos.entities.Role;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.repositories.EventAttendanceRepository;
import com.proyectos.organizacion_eventos.repositories.FeedbackEventRepository;
import com.proyectos.organizacion_eventos.repositories.GroupUserRepository;
import com.proyectos.organizacion_eventos.repositories.RoleRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final FeedbackEventRepository feedbackEventRepository;

    private final GroupUserRepository groupUserRepository;

    private final EventAttendanceRepository eventAttendanceRepository;

    private final UserRepository repository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;  

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
    public List<UserDTO> findAll() {

        List<User> users = (List<User>) repository.findAll();

        // Convertimos la lista de usuarios a una lista de UserDTO
        // Usamos stream para mapear cada User a UserDTO
        List<UserDTO> userDTOs = StreamSupport.stream(users.spliterator(), false)
            .map(user -> UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .email(user.getEmail())
            .build())
        .toList();
    
        // Retornamos la lista de UserDTO
        return userDTOs;
    }

    @Transactional
    @Override
    public User save(User user) {

        if (repository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        // Creamos la lista de roles que vendra con el usuario
        Set<Role> roles = new HashSet<>();

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


    // Metodo para obtener un User sin DTO por ID, usamos el repository
    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(int id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Optional<User> deleteById(int id) {
        Optional<User> userOptional = repository.findById(id);
        userOptional.ifPresent(user -> {

             // Borrar filas en tablas relacionadas primero
            eventAttendanceRepository.deleteByUserId(user.getId());
            feedbackEventRepository.deleteByUserId(user.getId());
            groupUserRepository.deleteByUserId(user.getId());

            repository.delete(user);
        });
        return userOptional;
    }

    // Metodo para obtener un UserDTO por ID, usamos el service
    @Transactional(readOnly = true)
    @Override
    public Optional<UserDTO> findByIdDTO(int id) {     
        return repository.findById(id)
        .map(user -> UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .email(user.getEmail())
            .build());
        }

    @Override
    @Transactional
    public Optional<User> update(UserUpdateDTO user, int id) {
        return repository.findById(id)
            .map(userExist -> {
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    userExist.setEmail(user.getEmail());
                }
                if (user.getName() != null && !user.getName().isBlank()) {
                    userExist.setName(user.getName());
                }                
                if (user.getUsername() != null && !user.getUsername().isBlank()) {
                userExist.setUsername(user.getUsername());
                }                
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    String encodedPassword = passwordEncoder.encode(user.getPassword());
                    userExist.setPassword(encodedPassword); // Encriptamos la contraseña
                }
                return repository.save(userExist);
            });

    }
}
