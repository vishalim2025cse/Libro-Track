package com.example.Libro.Track.repository;

import com.example.Libro.Track.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String title, String author, String category);
}
