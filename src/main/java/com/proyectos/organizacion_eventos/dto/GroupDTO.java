package com.proyectos.organizacion_eventos.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupDTO {
private int id;
    private String name;
    private List<MemberDTO> members;

    @Data
    @Builder
    public static class MemberDTO {
        private String name;
        private boolean isLeader;
    }
}
