package com.proyectos.organizacion_eventos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
