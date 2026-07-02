package com.conectatarot.backend.controller;

import com.conectatarot.backend.dto.ApiResponse;
import com.conectatarot.backend.dto.PerfilTarotistaDTO;
import com.conectatarot.backend.dto.TarotistaResponseDTO;
import com.conectatarot.backend.entity.Tarotista;
import com.conectatarot.backend.service.TarotistaService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.conectatarot.backend.repository.UsuarioRepository;
import com.conectatarot.backend.repository.RolRepository;
import com.conectatarot.backend.dto.RegistroTarotistaRequest;

import com.conectatarot.backend.entity.Usuario;

import com.conectatarot.backend.service.UsuarioService;
import com.conectatarot.backend.service.TarotistaEspecialidadService;
import com.conectatarot.backend.service.DisponibilidadTarotistaService;

import java.util.Map;


import java.util.List;

@RestController
@RequestMapping("/api/tarotistas")
@RequiredArgsConstructor
public class TarotistaController {

    private final TarotistaService tarotistaService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioService usuarioService;
    private final TarotistaEspecialidadService tarotistaEspecialidadService;
    private final DisponibilidadTarotistaService disponibilidadTarotistaService;



    @PostMapping("/registro")
    public ResponseEntity<?> registrarTarotista(
            @RequestBody RegistroTarotistaRequest request
    ) {
        tarotistaService.registrarTarotistaCompleto(request);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Tarotista registrado correctamente"
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Tarotista>> crearTarotista(
            @Valid @RequestBody CrearTarotistaRequest request
    ) {
        Tarotista tarotista = tarotistaService.crearTarotista(
                request.getUsuarioId(),
                request.getNombreProfesional()
        );

        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Tarotista creado correctamente", tarotista));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TarotistaResponseDTO>>> buscarTarotistas(
            @RequestParam(required = false) String especialidad
    ) {
        List<TarotistaResponseDTO> tarotistas =
                tarotistaService.buscarTarotistas(especialidad);

        return ResponseEntity.ok(
                ApiResponse.ok("Tarotistas obtenidos correctamente", tarotistas)
        );
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<ApiResponse<Tarotista>> actualizarPerfil(
            @PathVariable Integer id,
            @Valid @RequestBody PerfilTarotistaDTO request,
            Authentication authentication
    ) {
        Tarotista tarotista = tarotistaService.actualizarPerfil(
                id,
                authentication.getName(),
                request.getNombreProfesional(),
                request.getDescripcion(),
                request.getPrecioBase()
        );

        return ResponseEntity.ok(
                ApiResponse.ok("Perfil actualizado correctamente", tarotista)
        );
    }

    @PutMapping("/mi-perfil")
    public ResponseEntity<ApiResponse<Tarotista>> actualizarMiPerfil(
            @Valid @RequestBody PerfilTarotistaDTO request,
            Authentication authentication
    ) {
        Tarotista tarotista = tarotistaService.actualizarMiPerfil(
                authentication.getName(),
                request.getNombreProfesional(),
                request.getDescripcion(),
                request.getPrecioBase()
        );

        return ResponseEntity.ok(
                ApiResponse.ok("Perfil actualizado correctamente", tarotista)
        );
    }

    @Getter
    @Setter
    public static class CrearTarotistaRequest {
        private Integer usuarioId;
        private String nombreProfesional;
    }
  @PostMapping("/completar-perfil")
  public ResponseEntity<?> completarPerfil(@RequestBody java.util.Map<String, Object> body) {
     try {
        Integer usuarioId = (Integer) body.get("usuarioId");
        String nombreProfesional = (String) body.get("nombreProfesional");
        String descripcion = (String) body.get("descripcion");
        java.math.BigDecimal precioBase = new java.math.BigDecimal(body.get("precioBase").toString());
        String email = (String) body.get("email");

        Tarotista tarotista = tarotistaService.crearTarotista(usuarioId, nombreProfesional);
        tarotista = tarotistaService.actualizarPerfil(tarotista.getId(), email, nombreProfesional, descripcion, precioBase);

        var usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        usuario.setRol(rolRepository.findByNombreRol("TAROTISTA").orElseThrow());
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Perfil de tarotista creado", "tarotistaId", tarotista.getId()));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(java.util.Map.of("success", false, "message", e.getMessage()));
    }
  }
}
