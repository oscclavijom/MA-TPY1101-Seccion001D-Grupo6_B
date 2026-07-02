package com.conectatarot.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PerfilTarotistaDTO {

    @NotBlank(message = "El nombre profesional es obligatorio")
    private String nombreProfesional;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, max = 500, message = "La descripción debe tener entre 20 y 500 caracteres")
    private String descripcion;

    @DecimalMin(value = "1.0", message = "El precio base debe ser mayor a 0")
    private BigDecimal precioBase;

    private List<Integer> especialidades;

    private List<DisponibilidadRequestDTO> disponibilidades;
}