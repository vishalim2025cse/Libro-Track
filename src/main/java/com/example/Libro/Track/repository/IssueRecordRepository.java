package com.example.Libro.Track.repository;

import com.example.Libro.Track.entity.IssueRecord;
import com.example.Libro.Track.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {
    List<IssueRecord> findByStudentIdOrderByIssueDateDesc(Long studentId);
    List<IssueRecord> findByStudentIdAndStatusOrderByIssueDateDesc(Long studentId, IssueStatus status);
    List<IssueRecord> findByBookIdAndStatus(Long bookId, IssueStatus status);
    long countByStatus(IssueStatus status);
    boolean existsByBookIdAndStatus(Long bookId, IssueStatus status);
    boolean existsByStudentIdAndStatus(Long studentId, IssueStatus status);
}
