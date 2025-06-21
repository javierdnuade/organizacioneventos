package com.proyectos.organizacion_eventos.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyectos.organizacion_eventos.dto.EventDTO;
import com.proyectos.organizacion_eventos.entities.Event;
import com.proyectos.organizacion_eventos.repositories.EventRepository;

@Service
public class EventServiceImpl implements EventService{

    @Autowired
    private EventRepository repository;

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
                .status(event.getStatus().getName())
                .organizer(event.getOrganizer().getName())
                .build())
            .toList();
    }

    @Override
    public Event save(Event event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public Optional<Event> deleteById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public Optional<EventDTO> getEventDTO(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEventDTO'");
    }

    @Override
    public Optional<Event> findById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<EventDTO> findByStatusId(int statusId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByStatusId'");
    }

    @Override
    public Optional<Event> delete(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
