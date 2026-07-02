package com.conectatarot.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DisponibilidadResponseDTO {

    private String diaSemana;
    private String horaInicio;
    private String horaFin;
}
