package com.example.crudrapido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioExternoDTO {
    private String firstName;
    private String lastName;
    private String email;
}
