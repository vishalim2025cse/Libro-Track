package com.example.Libro.Track.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Author is required") String author,
        @NotBlank(message = "ISBN is required") String isbn,
        @NotBlank(message = "Category is required") String category,
        @Min(value = 1, message = "Total copies must be at least 1") int totalCopies
) {}
