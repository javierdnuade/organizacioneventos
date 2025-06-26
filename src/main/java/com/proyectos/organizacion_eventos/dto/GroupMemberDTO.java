package com.proyectos.organizacion_eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDTO {

    private int id;
    private String name;
    private String memberName;
    private boolean isLeader;

}
