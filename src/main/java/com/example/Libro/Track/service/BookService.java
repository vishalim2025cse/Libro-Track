package com.example.Libro.Track.service;

import com.example.Libro.Track.dto.BookRequest;
import com.example.Libro.Track.dto.BookResponse;
import com.example.Libro.Track.entity.Book;
import com.example.Libro.Track.entity.IssueStatus;
import com.example.Libro.Track.exception.BusinessRuleException;
import com.example.Libro.Track.exception.ResourceNotFoundException;
import com.example.Libro.Track.repository.BookRepository;
import com.example.Libro.Track.repository.IssueRecordRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final IssueRecordRepository issueRecordRepository;

    public BookService(BookRepository bookRepository, IssueRecordRepository issueRecordRepository) {
        this.bookRepository = bookRepository;
        this.issueRecordRepository = issueRecordRepository;
    }

    @Transactional
    public BookResponse addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new BusinessRuleException("A book with ISBN " + request.isbn() + " already exists.");
        }

        Book book = new Book();
        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setIsbn(request.isbn().trim());
        book.setCategory(request.category().trim());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.totalCopies());

        return toResponse(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String keyword) {
        String q = keyword.trim();
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(q, q, q)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return toResponse(findBook(id));
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBook(id);

        if (!book.getIsbn().equals(request.isbn()) && bookRepository.existsByIsbn(request.isbn())) {
            throw new BusinessRuleException("Another book already uses ISBN " + request.isbn() + ".");
        }

        int issuedCopies = book.getTotalCopies() - book.getAvailableCopies();
        if (request.totalCopies() < issuedCopies) {
            throw new BusinessRuleException(
                    "Total copies cannot be less than currently issued copies (" + issuedCopies + ").");
        }

        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setIsbn(request.isbn().trim());
        book.setCategory(request.category().trim());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.totalCopies() - issuedCopies);

        return toResponse(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = findBook(id);
        if (issueRecordRepository.existsByBookIdAndStatus(id, IssueStatus.ISSUED)) {
            throw new BusinessRuleException("Book cannot be deleted while a copy is currently issued.");
        }
        try {
            bookRepository.delete(book);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("Book cannot be deleted because it has issue history.");
        }
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
    }

    private BookResponse toResponse(Book book) {
        int issuedCopies = book.getTotalCopies() - book.getAvailableCopies();
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getCategory(), book.getTotalCopies(), book.getAvailableCopies(), issuedCopies);
    }
}
