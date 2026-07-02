package com.conectatarot.backend.service;

import com.conectatarot.backend.dto.DisponibilidadRequestDTO;
import com.conectatarot.backend.dto.RegistroTarotistaRequest;
import com.conectatarot.backend.dto.TarotistaResponseDTO;
import com.conectatarot.backend.entity.Tarotista;
import com.conectatarot.backend.entity.Usuario;
import com.conectatarot.backend.repository.RolRepository;
import com.conectatarot.backend.repository.TarotistaRepository;
import com.conectatarot.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarotistaService {

    private final TarotistaRepository tarotistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final TarotistaEspecialidadService tarotistaEspecialidadService;
    private final DisponibilidadTarotistaService disponibilidadTarotistaService;

    public Tarotista crearTarotista(Integer usuarioId, String nombreProfesional) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean yaExiste = tarotistaRepository.existsByUsuario_IdUsuario(usuarioId);
        if (yaExiste) {
            throw new RuntimeException("El usuario ya tiene un perfil de tarotista");
        }

        Tarotista tarotista = Tarotista.builder()
                .usuario(usuario)
                .nombreProfesional(nombreProfesional)
                .estado("PENDIENTE")
                .build();

        return tarotistaRepository.save(tarotista);
    }

    public Tarotista actualizarPerfil(
            Integer tarotistaId,
            String emailUsuarioLogueado,
            String descripcion,
            BigDecimal precioBase
    ) {
        Tarotista tarotista = tarotistaRepository.findById(tarotistaId)
                .orElseThrow(() -> new RuntimeException("Tarotista no encontrado"));

        if (!tarotista.getUsuario().getEmail().equals(emailUsuarioLogueado)) {
            throw new RuntimeException("No tienes permiso para editar este perfil");
        }

        if (descripcion == null || descripcion.length() < 20) {
            throw new RuntimeException("La descripción debe tener al menos 20 caracteres");
        }

        if (precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio base debe ser mayor a 0");
        }

        tarotista.setDescripcion(descripcion);
        tarotista.setPrecioBase(precioBase);

        return tarotistaRepository.save(tarotista);
    }

    public Tarotista actualizarPerfilCompleto(
            Integer tarotistaId,
            String emailUsuarioLogueado,
            String nombreProfesional,
            String descripcion,
            BigDecimal precioBase,
            List<Integer> especialidades,
            List<DisponibilidadRequestDTO> disponibilidades
    ) {
        Tarotista tarotista = tarotistaRepository.findById(tarotistaId)
                .orElseThrow(() -> new RuntimeException("Tarotista no encontrado"));

        if (!tarotista.getUsuario().getEmail().equals(emailUsuarioLogueado)) {
            throw new RuntimeException("No tienes permiso para editar este perfil");
        }

        if (nombreProfesional == null || nombreProfesional.isBlank()) {
            throw new RuntimeException("El nombre profesional es obligatorio");
        }

        if (descripcion == null || descripcion.length() < 20) {
            throw new RuntimeException("La descripción debe tener al menos 20 caracteres");
        }

        if (precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio base debe ser mayor a 0");
        }

        tarotista.setNombreProfesional(nombreProfesional);
        tarotista.setDescripcion(descripcion);
        tarotista.setPrecioBase(precioBase);

        if (especialidades != null && !especialidades.isEmpty()) {
            tarotistaEspecialidadService.reemplazarEspecialidades(tarotistaId, especialidades);
        }

        if (disponibilidades != null && !disponibilidades.isEmpty()) {
            disponibilidadTarotistaService.reemplazarDisponibilidades(tarotistaId, disponibilidades);
        }

        return tarotistaRepository.save(tarotista);
    }

    public List<TarotistaResponseDTO> buscarTarotistas(String especialidad) {

        List<Tarotista> tarotistas;

        if (especialidad != null && !especialidad.isBlank()) {
            tarotistas = tarotistaRepository
                    .findByEstadoIgnoreCaseAndTarotistaEspecialidades_Especialidad_NombreContainingIgnoreCase(
                            "APROBADO",
                            especialidad
                    );
        } else {
            tarotistas = tarotistaRepository.findByEstadoIgnoreCase("APROBADO");
        }

        return tarotistas.stream()
                .map(this::convertirADTO)
                .toList();
    }

    private TarotistaResponseDTO convertirADTO(Tarotista tarotista) {
        return TarotistaResponseDTO.builder()
                .id(tarotista.getId())
                .nombreProfesional(tarotista.getNombreProfesional())
                .descripcion(tarotista.getDescripcion())
                .precioBase(tarotista.getPrecioBase())
                .estado(tarotista.getEstado())
                .especialidades(
                        tarotista.getTarotistaEspecialidades()
                                .stream()
                                .map(relacion -> relacion.getEspecialidad().getNombre())
                                .toList()
                )
                .build();
    }

    @Transactional
    public void registrarTarotistaCompleto(RegistroTarotistaRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());

        usuario = usuarioService.registrarUsuarioConRol(usuario, "TAROTISTA");

        Tarotista tarotista = crearTarotista(
                usuario.getIdUsuario(),
                request.getNombreProfesional()
        );

        actualizarPerfil(
                tarotista.getId(),
                usuario.getEmail(),
                request.getDescripcion(),
                request.getPrecioBase()
        );

        for (Integer especialidadId : request.getEspecialidades()) {
            tarotistaEspecialidadService.agregarEspecialidad(
                    tarotista.getId(),
                    especialidadId
            );
        }

        if (request.getDisponibilidades() != null) {
            for (var d : request.getDisponibilidades()) {
                disponibilidadTarotistaService.crearDisponibilidad(
                        tarotista.getId(),
                        d.getDiaSemana(),
                        d.getHoraInicio(),
                        d.getHoraFin()
                );
            }
        }
    }
}