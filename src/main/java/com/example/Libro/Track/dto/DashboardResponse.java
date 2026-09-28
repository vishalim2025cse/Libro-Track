package com.example.Libro.Track.dto;

public record DashboardResponse(
        long totalBooks,
        long totalStudents,
        long totalCopies,
        long availableCopies,
        long currentlyIssued,
        long returnedRecords,
        double totalFineCollected
) {}
