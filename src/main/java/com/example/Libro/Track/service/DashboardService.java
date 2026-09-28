package com.example.Libro.Track.service;

import com.example.Libro.Track.dto.DashboardResponse;
import com.example.Libro.Track.entity.IssueStatus;
import com.example.Libro.Track.repository.BookRepository;
import com.example.Libro.Track.repository.IssueRecordRepository;
import com.example.Libro.Track.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final IssueRecordRepository issueRecordRepository;

    public DashboardService(BookRepository bookRepository,
                            StudentRepository studentRepository,
                            IssueRecordRepository issueRecordRepository) {
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
        this.issueRecordRepository = issueRecordRepository;
    }

    public DashboardResponse getDashboard() {
        long totalCopies = bookRepository.findAll().stream().mapToLong(b -> b.getTotalCopies()).sum();
        long availableCopies = bookRepository.findAll().stream().mapToLong(b -> b.getAvailableCopies()).sum();
        long currentlyIssued = issueRecordRepository.countByStatus(IssueStatus.ISSUED);
        long returnedRecords = issueRecordRepository.countByStatus(IssueStatus.RETURNED);
        double totalFine = issueRecordRepository.findAll().stream().mapToDouble(r -> r.getFineAmount()).sum();

        return new DashboardResponse(
                bookRepository.count(),
                studentRepository.count(),
                totalCopies,
                availableCopies,
                currentlyIssued,
                returnedRecords,
                totalFine
        );
    }
}
