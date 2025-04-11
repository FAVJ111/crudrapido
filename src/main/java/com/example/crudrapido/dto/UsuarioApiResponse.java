package com.example.crudrapido.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioApiResponse {
    private String name;
    private String email;
}
