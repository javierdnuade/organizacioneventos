package com.proyectos.organizacion_eventos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.EventAttendance;
import com.proyectos.organizacion_eventos.entities.Group;
import com.proyectos.organizacion_eventos.entities.GroupUser;
import com.proyectos.organizacion_eventos.entities.Status;
import com.proyectos.organizacion_eventos.entities.User;
import com.proyectos.organizacion_eventos.entities.embeddable.EventUserId;
import com.proyectos.organizacion_eventos.repositories.EventAttendanceRepository;
import com.proyectos.organizacion_eventos.repositories.EventRepository;
import com.proyectos.organizacion_eventos.repositories.StatusRepository;
import com.proyectos.organizacion_eventos.repositories.UserRepository;

@Service
public class EventServiceImpl implements EventService{

    @Autowired
    private EventRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
            .map(event -> EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .date(event.getDate())
                .location(event.getLocation())
                .status(event.getStatus().getDescription())
                .organizer(event.getOrganizer().getName())
                .build())
            .toList();
    }

    @Override
    @Transactional
    public Event save(Event event) {
        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha del evento debe ser futura");
        }

        User organizer = userRepository.findById(event.getOrganizer().getId())
            .orElseThrow(() -> new IllegalArgumentException("Organizador no válido"));

        Status status = statusRepository.findById(event.getStatus().getId())
            .orElseThrow(() -> new IllegalArgumentException("Status no encontrado"));

        event.setOrganizer(organizer);
        event.setStatus(status);

        return repository.save(event);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<EventDTO> getEventDTO(int id) {
        return repository.findById(id)
            .map(event -> EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .date(event.getDate())
                .location(event.getLocation())
                .status(event.getStatus().getDescription())
                .organizer(event.getOrganizer().getName())
                .build());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Event> findById(int id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> findByStatus(String string) {

        // Validación del estado
        if (string == null || string.isBlank() ||
            !(string.equalsIgnoreCase("Cancelado") ||
            string.equalsIgnoreCase("Finalizado") ||
            string.equalsIgnoreCase("Proximamente"))) {
            
                throw new IllegalArgumentException("El estado no puede ser nulo o diferente a los existentes");

        }
        // Filtrar eventos por estado
        return this.findAll().stream()
            .filter(event -> event.getStatus().equalsIgnoreCase(string))
            .toList();
    }

    @Override
    @Transactional
    public Optional<Event> delete(int id) {
        Optional<Event> eventOptional = repository.findById(id);
        eventOptional.ifPresent(event -> {
            // Se eliminan las relaciones existentes en la tabla intermedia con los grupos para que no haya problemas de eliminacion de claves foraneas
            event.clearGroups();

            // Hacemos un save del evento con las relaciones borradas para que Hibernate pueda hacer el borrado bien
            repository.save(event);  // para sincronizar las relaciones
            repository.flush();

            // Borramos el evento
            repository.delete(event);
        });
        return eventOptional;
    }

    @Override
    @Transactional
    public void addMember(int eventId, int userId, String usernameAuth) {
        Event event = repository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validamos que el usuario que manda la solicitud exista
        User userAuth = userRepository.findByUsername(usernameAuth)
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // Validamos que el usuario no esté inscrito en el evento
        EventUserId eventUserId = new EventUserId(eventId, userId);
        if (eventAttendanceRepository.existsById(eventUserId)) {
            throw new RuntimeException("El usuario ya está inscrito en el evento");
        }

        // Validamos que el usuario este dentro de algun grupo del evento

        Set<Group> groups = event.getGroups(); // Grupos que tiene el evento
        List<GroupUser> userGroupLinks = user.getGroups(); // Relación con sus grupos

        boolean pertenece = userGroupLinks.stream()
            .map(GroupUser::getGroup)
            .anyMatch(groups::contains);

        if (!pertenece) {
            throw new RuntimeException("El usuario no pertenece a ningún grupo asociado al evento");
        }

        // Validamos que el usuario que manda la solicitud sea organizador del evento o rol admin

        boolean isAdmin = userAuth.getRoles().stream()
            .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        boolean isOrganizer = event.getOrganizer().getId() == userAuth.getId();

        if (!isAdmin && !isOrganizer) {
            throw new RuntimeException("El usuario autenticado no es administrador o organizador del evento");
        }
            
        // Creamos la asistencia al evento
        EventAttendance attendance = new EventAttendance(eventUserId, event, user, false);
        // Guardamos la asistencia
        eventAttendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public void removeMember(int eventId, int userId, String usernameAuth) {
        // Validamos que el evento exista
        Event event = repository.findById(eventId)
            .orElseThrow( () -> new RuntimeException("Evento no encontrado"));


        // Buscamos al usuario autenticado en la base
        User userAuth = userRepository.findByUsername(usernameAuth)
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // Validamos que el usuario que manda la solicitud sea organizador del evento o rol admin

        boolean isAdmin = userAuth.getRoles().stream()
            .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        boolean isOrganizer = event.getOrganizer().getId() == userAuth.getId();
        
        if (!isAdmin && !isOrganizer) {
            throw new RuntimeException("El usuario no es admin o organizador del evento");
        }

        // Validamos que esté inscrito en el evento
        EventUserId id = new EventUserId(eventId, userId);
        if (!eventAttendanceRepository.existsById(id)) {
            throw new RuntimeException("El usuario no está inscrito en el evento");
        }

        // Eliminamos la asistencia
        eventAttendanceRepository.deleteById(id);
    }
}
