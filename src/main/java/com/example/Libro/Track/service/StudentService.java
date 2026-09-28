package com.example.Libro.Track.service;

import com.example.Libro.Track.dto.StudentRequest;
import com.example.Libro.Track.dto.StudentResponse;
import com.example.Libro.Track.entity.Student;
import com.example.Libro.Track.entity.IssueStatus;
import com.example.Libro.Track.exception.BusinessRuleException;
import com.example.Libro.Track.exception.ResourceNotFoundException;
import com.example.Libro.Track.repository.IssueRecordRepository;
import com.example.Libro.Track.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final IssueRecordRepository issueRecordRepository;

    public StudentService(StudentRepository studentRepository, IssueRecordRepository issueRecordRepository) {
        this.studentRepository = studentRepository;
        this.issueRecordRepository = issueRecordRepository;
    }

    @Transactional
    public StudentResponse addStudent(StudentRequest request) {
        if (studentRepository.existsByEmail(request.email().trim())) {
            throw new BusinessRuleException("A student with this email already exists.");
        }

        Student student = new Student();
        student.setName(request.name().trim());
        student.setEmail(request.email().trim());
        return toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        return toResponse(findStudent(id));
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = findStudent(id);

        if (!student.getEmail().equalsIgnoreCase(request.email().trim())
                && studentRepository.existsByEmail(request.email().trim())) {
            throw new BusinessRuleException("Another student already uses this email.");
        }

        student.setName(request.name().trim());
        student.setEmail(request.email().trim());
        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findStudent(id);
        if (issueRecordRepository.existsByStudentIdAndStatus(id, IssueStatus.ISSUED)) {
            throw new BusinessRuleException("Student cannot be deleted while a book is currently issued.");
        }
        studentRepository.delete(student);
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getName(), student.getEmail());
    }
}
