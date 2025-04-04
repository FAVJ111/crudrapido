package com.example.crudrapido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crudrapido.entity.Parametrizacion;

@Repository
public interface ParametrizacionRepository extends JpaRepository<Parametrizacion, Long> {
    Optional<Parametrizacion> findByClave(String clave);
}
