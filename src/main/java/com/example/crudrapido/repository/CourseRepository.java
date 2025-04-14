package com.example.crudrapido.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.crudrapido.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
