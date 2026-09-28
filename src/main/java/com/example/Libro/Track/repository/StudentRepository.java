package com.example.Libro.Track.repository;

import com.example.Libro.Track.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);
}
