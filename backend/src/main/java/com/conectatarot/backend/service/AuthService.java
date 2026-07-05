package com.conectatarot.backend.service;

import com.conectatarot.backend.dto.LoginRequestDTO;
import com.conectatarot.backend.dto.LoginResponseDTO;
import com.conectatarot.backend.entity.Usuario;
import com.conectatarot.backend.exception.ForbiddenException;
import com.conectatarot.backend.repository.TarotistaRepository;
import com.conectatarot.backend.repository.UsuarioRepository;
import com.conectatarot.backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TarotistaRepository tarotistaRepository;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TarotistaRepository tarotistaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tarotistaRepository = tarotistaRepository;
    }

    public LoginResponseDTO login(
            LoginRequestDTO request
    ) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("Usuario no encontrado")
                );

        if(!passwordEncoder.matches(
                request.getPassword(),
                usuario.getPassword()
        )){
            throw new RuntimeException("Credenciales inválidas");
        }

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new ForbiddenException(
                    "Tu cuenta ha sido bloqueada por un administrador"
            );
        }

        if ("TAROTISTA".equals(usuario.getRol().getNombreRol())) {
            tarotistaRepository.findByUsuario_Email(usuario.getEmail())
                    .ifPresentOrElse(
                            tarotista -> {
                                if (!"APROBADO".equals(tarotista.getEstado())) {
                                    throw new ForbiddenException(
                                            "Tu cuenta de tarotista aún no ha sido aprobada por un administrador"
                                    );
                                }
                            },
                            () -> {
                                throw new ForbiddenException(
                                        "No se encontró el registro de tarotista asociado a tu cuenta"
                                );
                            }
                    );
        }

        String token = jwtService.generateToken(
                usuario.getEmail(),
                usuario.getRol().getNombreRol()
        );

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombreRol(),
                usuario.getActivo(),
                token
        );
    }

}