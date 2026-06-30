package com.conectatarot.backend.controller;

import com.conectatarot.backend.dto.ApiResponse;
import com.conectatarot.backend.dto.PageResponse;
import com.conectatarot.backend.dto.SesionRequestDTO;
import com.conectatarot.backend.dto.SesionResponseDTO;
import com.conectatarot.backend.entity.Sesion;
import com.conectatarot.backend.service.SesionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Sesion>> agendarSesion(
            @Valid @RequestBody SesionRequestDTO request
    ) {
        Sesion sesion = sesionService.agendarSesion(request);

        return ResponseEntity.ok(
                ApiResponse.ok("Sesión agendada correctamente", sesion)
        );
    }

    @GetMapping("/mis-sesiones")
    public ResponseEntity<ApiResponse<List<SesionResponseDTO>>> obtenerMisSesiones(
            Authentication authentication
    ) {
        List<SesionResponseDTO> sesiones =
                sesionService.obtenerMisSesiones(authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.ok("Sesiones obtenidas correctamente", sesiones)
        );
    }

    @GetMapping("/mis-sesiones/historial")
    public ResponseEntity<ApiResponse<List<SesionResponseDTO>>> obtenerMisSesionesHistorial(
            Authentication authentication
    ) {
        List<SesionResponseDTO> sesiones =
                sesionService.obtenerMisSesionesHistorial(authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.ok("Historial de sesiones obtenido correctamente", sesiones)
        );
    }

    @GetMapping("/mis-pagos")
    public ResponseEntity<ApiResponse<List<SesionResponseDTO>>> obtenerMisPagos(
            Authentication authentication
    ) {
        List<SesionResponseDTO> pagos =
                sesionService.obtenerMisPagos(authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.ok("Pagos obtenidos correctamente", pagos)
        );
    }

    @GetMapping("/tarotista")
    public ResponseEntity<ApiResponse<PageResponse<SesionResponseDTO>>> obtenerSesionesTarotista(
            Authentication authentication,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<SesionResponseDTO> sesionesPage =
                sesionService.obtenerSesionesTarotistaPaginado(
                        authentication.getName(),
                        estado,
                        pageable
                );

        PageResponse<SesionResponseDTO> response = PageResponse.<SesionResponseDTO>builder()
                .content(sesionesPage.getContent())
                .page(sesionesPage.getNumber())
                .size(sesionesPage.getSize())
                .totalElements(sesionesPage.getTotalElements())
                .totalPages(sesionesPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.ok("Agenda obtenida correctamente", response)
        );
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<SesionResponseDTO>> cancelarSesion(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        SesionResponseDTO sesion =
                sesionService.cancelarSesion(id, authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.ok("Sesión cancelada correctamente", sesion)
        );
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<ApiResponse<SesionResponseDTO>> confirmarSesion(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        SesionResponseDTO sesion =
                sesionService.confirmarSesion(id, authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.ok("Sesión confirmada correctamente", sesion)
        );
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<ApiResponse<SesionResponseDTO>> rechazarSesion(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        SesionResponseDTO sesion =
                sesionService.rechazarSesion(id, authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.ok("Sesión rechazada correctamente", sesion)
        );
    }
}