package com.example.Libro.Track.dto;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        String category,
        int totalCopies,
        int availableCopies,
        int issuedCopies
) {}
