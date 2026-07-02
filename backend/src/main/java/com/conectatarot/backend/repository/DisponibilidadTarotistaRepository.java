package com.conectatarot.backend.repository;

import com.conectatarot.backend.entity.DisponibilidadTarotista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilidadTarotistaRepository extends JpaRepository<DisponibilidadTarotista, Integer> {

    List<DisponibilidadTarotista> findByTarotistaIdAndActivaTrue(Integer tarotistaId);

}