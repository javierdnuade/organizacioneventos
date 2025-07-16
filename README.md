[ESP]
Proyecto: Sistema de Organización de Eventos
Este proyecto es una aplicación backend desarrollada en Java con Spring Boot para gestionar eventos, usuarios, grupos y feedbacks con el fin de mejorar mis habilidades en el framework mencionado

Funcionalidades principales
- Gestión de usuarios con roles y autenticación.
- Creación y administración de eventos.
- Inscripción de usuarios a eventos y gestión de asistencia.
- Gestión de grupos y su relación con usuarios y eventos.
- Sistema de feedback para eventos y usuarios.
- Control de permisos para administradores y organizadores.

Tecnologías
- Java 17+
- Spring Boot
- Hibernate / JPA
- MySQL
- Seguridad con Spring Security

- Relaciones en DB:
Un Usuario puede organizar Muchos eventos.
Un Usuario puede participar de Muchos eventos
Un Usuario puede estar en Muchos grupos (como también liderar alguno de ellos, eso se define dentro de la entidad del GrupoUsuario)
Un Usuarios puede tener Muchos Roles (Admin y User)

Un Evento tiene Un organizador
Un Evento tiene Muchos participantes (tabla intermedia AsistenciaEvento )
Un Evento tiene Un feedback de cada participante (tabla intermedia FeedbackEvento)
Un Evento puede estar en Muchos grupos (tabla intermedia evento_grupos)
Un Evento tiene un Status

Un Grupo puede tener Muchos Usuarios (ManyToMany también, por lo que habrá tabla intermedia grupo_usuarios)

[ENG]
Project: Event Organization System
This project is a backend application developed in Java with Spring Boot to manage events, users, groups, and feedbacks with the goal of improving my skills in the mentioned framework.

Main Features
- User management with roles and authentication.
- Creation and administration of events.
- User registration to events and attendance management.
- Group management and its relation to users and events.
- Feedback system for events and users.
- Permissions control for administrators and organizers.

Technologies
- Java 17+
- Spring Boot
- Hibernate / JPA
- MySQL
- Security with Spring Security

- Relationships on DB:

A User can organize many events.
A User can participate in many events.
A User can belong to many groups (and may lead some of them, which is defined within the GroupUser entity).
A User can have many Roles (Admin and User).

An Event has one organizer.
An Event has many participants (through the intermediate table EventAttendance).
An Event has one feedback per participant (through the intermediate table FeedbackEvent).
An Event can belong to many groups (through the intermediate table event_groups).
An Event has a Status.

A Group can have many Users (also ManyToMany, so there is an intermediate table group_users).



