package com.example.Spring_Boot;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.Spring_Boot.Entity.Book;
import com.example.Spring_Boot.repository.BookRepository;
import com.example.Spring_Boot.service.BookService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private List<Book> books;

    @BeforeEach
    void setUp() {
        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book One");
        book1.setAuthor("Author One");
        book1.setPrice(new BigDecimal("29.99"));
        book1.setPublishedDate(LocalDate.of(2024, 1, 1));

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book Two");
        book2.setAuthor("Author Two");
        book2.setPrice(new BigDecimal("39.99"));
        book2.setPublishedDate(LocalDate.of(2024, 2, 1));

        books = Arrays.asList(book1, book2);
    }

    // ✅ Test for retrieving all books
    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Book One", result.get(0).getTitle());
        assertEquals("Book Two", result.get(1).getTitle());
    }

    // ✅ Test for retrieving a book by ID (success case)
    @Test
    void testGetBookById_Found() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("Book One", result.get().getTitle());
    }

    // ✅ Test for retrieving a book by ID (not found case)
    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(3L);

        assertFalse(result.isPresent());
    }

    // ✅ Test for adding a new book
    @Test
    void testAddBook() {
        when(bookRepository.save(book1)).thenReturn(book1);

        Book savedBook = bookService.addBook(book1);

        assertNotNull(savedBook);
        assertEquals("Book One", savedBook.getTitle());
        assertEquals("Author One", savedBook.getAuthor());
    }

    // ✅ Test for updating an existing book (success case)
    @Test
    void testUpdateBook_Success() {
        Book updatedBook = new Book();
        updatedBook.setTitle("Updated Book");
        updatedBook.setAuthor("Updated Author");
        updatedBook.setPrice(new BigDecimal("49.99"));
        updatedBook.setPublishedDate(LocalDate.of(2025, 1, 1));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        Book result = bookService.updateBook(1L, updatedBook);

        assertNotNull(result);
        assertEquals("Updated Book", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
        assertEquals(new BigDecimal("49.99"), result.getPrice());
        assertEquals(LocalDate.of(2025, 1, 1), result.getPublishedDate());
    }

    // ✅ Test for updating a non-existing book (exception case)
    @Test
    void testUpdateBook_NotFound() {
        when(bookRepository.findById(3L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.updateBook(3L, book1);
        });

        assertEquals("Book not found", exception.getMessage());
    }

    // ✅ Test for deleting a book
    @Test
    void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));

        verify(bookRepository, times(1)).deleteById(1L);
    }
}
