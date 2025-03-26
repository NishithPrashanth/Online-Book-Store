package com.example.Spring_Boot.controller;

import com.example.Spring_Boot.service.BookService;
import com.example.Spring_Boot.Entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private BookService bookService;

    // Add a new book
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        logger.info("Received request to add book: {}", book.getTitle());
        Book savedBook = bookService.addBook(book);
        logger.info("Book added successfully with ID: {}", savedBook.getId());
        return savedBook;
    }

    // Retrieve all books
    @GetMapping
    public List<Book> getAllBooks() {
        logger.info("Received request to fetch all books");
        List<Book> books = bookService.getAllBooks();
        logger.info("Returning {} books", books.size());
        return books;
    }

    // Retrieve a book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        logger.info("Received request to fetch book with ID: {}", id);
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            logger.info("Book found: {}", book.get().getTitle());
            return ResponseEntity.ok(book.get());
        } else {
            logger.warn("Book not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // Update an existing book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        logger.info("Received request to update book with ID: {}", id);
        Book updatedBook = bookService.updateBook(id, book);
        logger.info("Book updated successfully: {}", updatedBook.getTitle());
        return ResponseEntity.ok(updatedBook);
    }

    @GetMapping("/books/{id}")
public ResponseEntity<Book> getBb(@PathVariable Long id) {
    Optional<Book> book = bookService.getBookById(id);
    return book.map(ResponseEntity::ok)
               .orElseGet(() -> ResponseEntity.notFound().build());
}


    // Delete a book by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        logger.warn("Received request to delete book with ID: {}", id);
        bookService.deleteBook(id);
        logger.warn("Book deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
