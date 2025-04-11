package com.example.crudrapido.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.crudrapido.dto.UsuarioApiResponse;
import com.example.crudrapido.dto.UsuarioExternoDTO;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/usuarios-externos")
public class UsuarioExternoController {

    private final RestTemplate restTemplate;

    @Autowired
    public UsuarioExternoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Operation(summary = "Obtener usuario externo por ID", description = "Retorna el nombre, apellido y correo electrónico de un usuario externo desde una API pública")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioExternoDTO> obtenerUsuarioExterno(@PathVariable Long id) {
        // URL del API
        String url = "https://jsonplaceholder.typicode.com/users/" + id;
        UsuarioApiResponse usuarioApi = restTemplate.getForObject(url, UsuarioApiResponse.class);

        if (usuarioApi == null || usuarioApi.getName() == null) {
            return ResponseEntity.notFound().build();
        }

        String[] nombresPartes = usuarioApi.getName().split(" ", 2);
        String firstName = nombresPartes.length > 0 ? nombresPartes[0] : "";
        String lastName = nombresPartes.length > 1 ? nombresPartes[1] : "";

        UsuarioExternoDTO dto = new UsuarioExternoDTO(firstName, lastName, usuarioApi.getEmail());

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Obtener todos los usuarios externos", description = "Retorna una lista de usuarios externos desde una API pública con nombre, apellido y email")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioExternoDTO>> obtenerTodosLosUsuariosExternos() {
        String url = "https://jsonplaceholder.typicode.com/users";
        UsuarioApiResponse[] usuariosApi = restTemplate.getForObject(url, UsuarioApiResponse[].class);

        if (usuariosApi == null || usuariosApi.length == 0) {
            return ResponseEntity.noContent().build();
        }

        List<UsuarioExternoDTO> usuarios = Arrays.stream(usuariosApi)
                .map(usuario -> {
                    String[] partes = usuario.getName().split(" ", 2);
                    String firstName = partes.length > 0 ? partes[0] : "";
                    String lastName = partes.length > 1 ? partes[1] : "";
                    return new UsuarioExternoDTO(firstName, lastName, usuario.getEmail());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuarios);
    }

}
