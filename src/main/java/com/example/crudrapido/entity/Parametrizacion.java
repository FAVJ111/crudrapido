package com.example.crudrapido.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "parametrizacion")
public class Parametrizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clave;  // Ejemplo: "MAX_ESTUDIANTES"

    @Column(nullable = false)
    private String valor;  // Ejemplo: "10"
}
