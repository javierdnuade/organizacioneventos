package com.proyectos.organizacion_eventos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.dto.EventParticipantDTO;
import com.proyectos.organizacion_eventos.dto.EventUpdateDTO;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository repository;

    private final UserRepository userRepository;

    private final StatusRepository statusRepository;

    private final EventAttendanceRepository eventAttendanceRepository;

    @Transactional(readOnly = true)
    @Override
    public List<EventDTO> findAll() {

        return (List<EventDTO>) StreamSupport.stream(repository.findAll().spliterator(), false)
            .map(event -> {
                List<EventParticipantDTO> participants = repository.findParticipantsByEventId(event.getId());

                return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .date(event.getDate())
                .location(event.getLocation())
                .status(event.getStatus().getDescription())
                .organizer(event.getOrganizer() != null ? event.getOrganizer().getName() : null)
                .attendance(participants)
                .build();
            })

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
        Optional<Event> eventOptional = repository.findById(id);
        if (eventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event event = eventOptional.get();
        List<EventParticipantDTO> participants = repository.findParticipantsByEventId(id);
        return Optional.of(EventDTO.builder()
            .id(event.getId())
            .name(event.getName())
            .description(event.getDescription())
            .date(event.getDate())
            .location(event.getLocation())
            .status(event.getStatus().getDescription())
            .organizer(event.getOrganizer() != null ? event.getOrganizer().getName() : null)
            .attendance(participants)
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
    public Optional<String> addMember(int eventId, int userId) {
       
        Optional<Event> eventOpt = repository.findById(eventId);
        if (eventOpt.isEmpty()) return Optional.of("Evento no encontrado");

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return Optional.of("Usuario no encontrado");

        Event event = eventOpt.get();
        User user = userOpt.get();

        EventUserId eventUserId = new EventUserId(eventId, userId);
        if (eventAttendanceRepository.existsById(eventUserId)) {
            return Optional.of("El usuario ya está inscrito en el evento");
        }

        // Validamos que el usuario este dentro de algun grupo del evento
        // Esta logica va aca, ya que es logica de negocio y no de autorizacion/autenticacion

        Set<Group> groups = event.getGroups(); // Grupos que tiene el evento
        List<GroupUser> userGroupLinks = user.getGroups(); // Relación con sus grupos

        boolean pertenece = userGroupLinks.stream()
            .map(GroupUser::getGroup)
            .anyMatch(groups::contains);

        if (!pertenece) {
            return Optional.of("El usuario no pertenece a ningun grupo del evento");
        }
            
        // Creamos la asistencia al evento
        EventAttendance attendance = new EventAttendance(eventUserId, event, user, false);
        // Guardamos la asistencia
        eventAttendanceRepository.save(attendance);
        return Optional.empty(); // No hay error, por lo que retornamos un Optional vacío
    }

    @Override
    @Transactional
    public Optional<EventAttendance> removeMember(int eventId, int userId) {

        // Validamos que esté inscrito en el evento
        EventUserId id = new EventUserId(eventId, userId);
        Optional<EventAttendance> attendanceOptional = eventAttendanceRepository.findById(id);
        attendanceOptional.ifPresent( att -> {
            eventAttendanceRepository.delete(att);
        });
        return attendanceOptional;
    }

    @Override
    public Optional<EventDTO> getParticipationForEventDTO(int id, boolean attendance) {
        Optional<Event> eventOptional = repository.findById(id);
        if (eventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event event = eventOptional.get();
        List<EventParticipantDTO> participants = repository.findParticipantsByAttended(id, attendance);
        return Optional.of(EventDTO.builder()
            .id(event.getId())
            .name(event.getName())
            .description(event.getDescription())
            .date(event.getDate())
            .location(event.getLocation())
            .status(event.getStatus().getDescription())
            .organizer(event.getOrganizer() != null ? event.getOrganizer().getName() : null)
            .attendance(participants)
            .build());
    }

    @Override
    public Boolean isOrganizer(int eventId, String username) {
        Optional<Event> eventOpt = repository.findById(eventId);
        
        if (eventOpt.isEmpty()) {
            return false;
        }

        Event event = eventOpt.get();
        User organizer = event.getOrganizer();
        if (organizer == null) {
            return null; // Evento sin organizador
        }
        return organizer.getUsername().equals(username);
    }

    @Override
    public Optional<Event> update(EventUpdateDTO event, int id) {
        return repository.findById(id)
            .map(eventExist -> {
                if (event.getName() != null && !event.getName().isBlank()) {
                    eventExist.setName(event.getName());
                }
                if (event.getDescription() != null && !event.getDescription().isBlank()) {
                    eventExist.setDescription(event.getDescription());
                }
                if (event.getDate() != null) {
                    if (event.getDate().isBefore(LocalDateTime.now())) {
                        throw new IllegalArgumentException("La fecha del evento debe ser en el futuro");
                    }
                    eventExist.setDate(event.getDate());
                }
                if (event.getLocation() != null && !event.getLocation().isBlank()) {
                    eventExist.setLocation(event.getLocation());
                }
                if (event.getStatus() != null) {
                    Optional<Status> status = statusRepository.findById(event.getStatus());
                    if (status.isPresent())
                    eventExist.setStatus(status.get());
                }
                return repository.save(eventExist);
            });
    }
}
