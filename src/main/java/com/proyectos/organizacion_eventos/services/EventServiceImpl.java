package com.proyectos.organizacion_eventos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.entities.Status;
import com.proyectos.organizacion_eventos.entities.User;
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
    public Optional<Event> delete(int id) {
        Optional<Event> eventOptional = repository.findById(id);
        eventOptional.ifPresent(event -> {
            repository.delete(event);
        });
        return eventOptional;
    }
}
