package com.proyectos.organizacion_eventos.services;

public interface EventAttendanceService {

    void setAttendance(int eventId, int userId, boolean attenden);

}
