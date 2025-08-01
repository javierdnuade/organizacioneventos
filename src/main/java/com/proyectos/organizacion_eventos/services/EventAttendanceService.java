package com.proyectos.organizacion_eventos.services;

import com.proyectos.organizacion_eventos.dto.EventAttendanceResponseDTO;

public interface EventAttendanceService {

    EventAttendanceResponseDTO setAttendance(int eventId, int userId, boolean attenden);

}
