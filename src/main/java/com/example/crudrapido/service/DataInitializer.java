package com.example.crudrapido.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.crudrapido.entity.Course;
import com.example.crudrapido.repository.CourseRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initCourses(CourseRepository courseRepository) {
        return args -> {
            courseRepository.save(Course.builder().name("Matemáticas").build());
            courseRepository.save(Course.builder().name("Historia").build());
        };
    }
}
