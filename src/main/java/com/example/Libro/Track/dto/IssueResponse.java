package com.example.Libro.Track.dto;

import java.time.LocalDate;

public record IssueResponse(
        Long id,
        Long bookId,
        String bookTitle,
        Long studentId,
        String studentName,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate returnDate,
        double fineAmount,
        String status
) {}
