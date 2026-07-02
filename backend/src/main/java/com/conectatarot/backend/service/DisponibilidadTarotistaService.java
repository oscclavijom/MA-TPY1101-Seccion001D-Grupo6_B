package com.conectatarot.backend.service;

import com.conectatarot.backend.entity.DisponibilidadTarotista;
import com.conectatarot.backend.entity.Tarotista;
import com.conectatarot.backend.repository.DisponibilidadTarotistaRepository;
import com.conectatarot.backend.repository.TarotistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DisponibilidadTarotistaService {

    private final DisponibilidadTarotistaRepository repository;
    private final TarotistaRepository tarotistaRepository;

    public void crearDisponibilidad(
        Integer tarotistaId,
        String dia,
        String horaInicio,
        String horaFin
        ) {

        LocalTime inicio = LocalTime.parse(horaInicio);
        LocalTime fin = LocalTime.parse(horaFin);

        if (!fin.isAfter(inicio)) {
                throw new RuntimeException(
                        "La hora de fin debe ser mayor que la hora de inicio"
                );
        }

        Tarotista tarotista = tarotistaRepository.findById(tarotistaId)
                .orElseThrow();

        DisponibilidadTarotista disponibilidad =
                DisponibilidadTarotista.builder()
                        .tarotista(tarotista)
                        .diaSemana(dia)
                        .horaInicio(inicio)
                        .horaFin(fin)
                        .activa(true)
                        .build();

        repository.save(disponibilidad);
        }
}