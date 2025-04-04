package com.example.crudrapido.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crudrapido.entity.Parametrizacion;

import jakarta.annotation.PostConstruct;


@Service
public class InitialDataService {

    @Autowired
    private ParametrizacionService parametrizacionService;

    @PostConstruct
    public void init() {
        if (!parametrizacionService.getByClave("MAX_ESTUDIANTES").isPresent()) {
            Parametrizacion param = new Parametrizacion();
            param.setClave("MAX_ESTUDIANTES");
            param.setValor("10");
            parametrizacionService.save(param);  // Método save() en ParametrizacionService
        }
    }
}
