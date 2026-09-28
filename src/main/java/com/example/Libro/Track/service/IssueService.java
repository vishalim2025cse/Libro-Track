package com.example.Libro.Track.service;

import com.example.Libro.Track.dto.IssueRequest;
import com.example.Libro.Track.dto.IssueResponse;
import com.example.Libro.Track.entity.Book;
import com.example.Libro.Track.entity.IssueRecord;
import com.example.Libro.Track.entity.IssueStatus;
import com.example.Libro.Track.entity.Student;
import com.example.Libro.Track.exception.BusinessRuleException;
import com.example.Libro.Track.exception.ResourceNotFoundException;
import com.example.Libro.Track.repository.BookRepository;
import com.example.Libro.Track.repository.IssueRecordRepository;
import com.example.Libro.Track.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class IssueService {

    private final IssueRecordRepository issueRecordRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final int loanDays;
    private final double finePerDay;

    public IssueService(IssueRecordRepository issueRecordRepository,
                        BookRepository bookRepository,
                        StudentRepository studentRepository,
                        @Value("${library.loan-days:14}") int loanDays,
                        @Value("${library.fine-per-day:5.0}") double finePerDay) {
        this.issueRecordRepository = issueRecordRepository;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
        this.loanDays = loanDays;
        this.finePerDay = finePerDay;
    }

    @Transactional
    public IssueResponse issueBook(IssueRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.bookId()));
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.studentId()));

        if (book.getAvailableCopies() <= 0) {
            throw new BusinessRuleException("Book cannot be issued because all copies are already checked out.");
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(loanDays);

        IssueRecord record = new IssueRecord();
        record.setBook(book);
        record.setStudent(student);
        record.setIssueDate(issueDate);
        record.setDueDate(dueDate);
        record.setFineAmount(0.0);
        record.setStatus(IssueStatus.ISSUED);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        return toResponse(issueRecordRepository.save(record));
    }

    @Transactional
    public IssueResponse returnBook(Long issueId) {
        IssueRecord record = issueRecordRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record not found with ID: " + issueId));

        if (record.getStatus() == IssueStatus.RETURNED) {
            throw new BusinessRuleException("This book has already been returned.");
        }

        LocalDate returnDate = LocalDate.now();
        double fine = calculateFine(record.getDueDate(), returnDate);

        record.setReturnDate(returnDate);
        record.setFineAmount(fine);
        record.setStatus(IssueStatus.RETURNED);

        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return toResponse(issueRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getAllIssues() {
        return issueRecordRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByStudent(Long studentId, boolean activeOnly) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }

        List<IssueRecord> records = activeOnly
                ? issueRecordRepository.findByStudentIdAndStatusOrderByIssueDateDesc(studentId, IssueStatus.ISSUED)
                : issueRecordRepository.findByStudentIdOrderByIssueDateDesc(studentId);

        return records.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public IssueResponse getIssue(Long id) {
        IssueRecord record = issueRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record not found with ID: " + id));
        return toResponse(record);
    }

    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (!returnDate.isAfter(dueDate)) {
            return 0.0;
        }
        long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return lateDays * finePerDay;
    }

    private IssueResponse toResponse(IssueRecord record) {
        return new IssueResponse(
                record.getId(),
                record.getBook().getId(),
                record.getBook().getTitle(),
                record.getStudent().getId(),
                record.getStudent().getName(),
                record.getIssueDate(),
                record.getDueDate(),
                record.getReturnDate(),
                record.getFineAmount(),
                record.getStatus().name()
        );
    }
}
