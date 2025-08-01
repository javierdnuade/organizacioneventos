package com.proyectos.organizacion_eventos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberResponseDTO {

    private String groupName;
    private String userName;
    private Boolean isLeader;
}
