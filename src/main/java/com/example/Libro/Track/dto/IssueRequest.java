package com.example.Libro.Track.dto;

import jakarta.validation.constraints.NotNull;

public record IssueRequest(
        @NotNull(message = "Book ID is required") Long bookId,
        @NotNull(message = "Student ID is required") Long studentId
) {}
