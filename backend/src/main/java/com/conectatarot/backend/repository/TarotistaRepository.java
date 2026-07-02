package com.conectatarot.backend.repository;

import com.conectatarot.backend.entity.Tarotista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarotistaRepository extends JpaRepository<Tarotista, Integer> {

    boolean existsByUsuario_IdUsuario(Integer usuarioId);

    List<Tarotista> findByEstadoIgnoreCase(String estado);

    long countByEstadoIgnoreCase(String estado);

    List<Tarotista> findByEstadoIgnoreCaseAndTarotistaEspecialidades_Especialidad_NombreContainingIgnoreCase(
            String estado,
            String especialidad
    );

    java.util.Optional<Tarotista> findByUsuario_Email(String email);
}