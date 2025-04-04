package com.example.crudrapido.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crudrapido.entity.Parametrizacion;
import com.example.crudrapido.repository.ParametrizacionRepository;

@Service
public class ParametrizacionService {
    @Autowired
    private ParametrizacionRepository parametrizacionRepository;

    public Optional<Parametrizacion> getByClave(String clave) {
        return parametrizacionRepository.findByClave(clave);
    }

    public void save(Parametrizacion parametrizacion) {
        parametrizacionRepository.save(parametrizacion);
    }
    
}
