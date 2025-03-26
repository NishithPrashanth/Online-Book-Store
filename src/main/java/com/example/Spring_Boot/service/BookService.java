package com.example.Spring_Boot.service;

import com.example.Spring_Boot.repository.BookRepository;
import com.example.Spring_Boot.Entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    // Add a new book
    public Book addBook(Book book) {
        logger.info("Adding new book: {}", book.getTitle());
        return bookRepository.save(book);
    }

    // Retrieve all books
    public List<Book> getAllBooks() {
        logger.info("Fetching all books");
        return bookRepository.findAll();
    }

    // Retrieve a book by its ID
    public Optional<Book> getBookById(Long id) {
        logger.info("Fetching book with ID: {}", id);
        return bookRepository.findById(id);
    }

    // Update an existing book
    public Book updateBook(Long id, Book bookDetails) {
        logger.info("Updating book with ID: {}", id);
        return bookRepository.findById(id).map(book -> {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setPrice(bookDetails.getPrice());
            book.setPublishedDate(bookDetails.getPublishedDate());
            logger.info("Book updated successfully: {}", book.getTitle());
            return bookRepository.save(book);
        }).orElseThrow(() -> {
            logger.error("Book not found with ID: {}", id);
            return new RuntimeException("Book not found");
        });
    }

    // Delete a book by its ID
    public void deleteBook(Long id) {
        logger.warn("Deleting book with ID: {}", id);
        bookRepository.deleteById(id);
    }
}
