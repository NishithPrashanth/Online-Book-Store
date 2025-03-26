package com.example.Spring_Boot;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.Spring_Boot.Entity.Book;
import com.example.Spring_Boot.controller.Controller;
import com.example.Spring_Boot.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(Controller.class) // Only load the Controller layer
class ControllerTest {

    @Autowired
    private MockMvc mockMvc; // ✅ Properly Injected

    @MockBean
    private BookService bookService; // ✅ Mocking the service

    @InjectMocks
    private Controller bookController;

    @Autowired
    private ObjectMapper objectMapper; // ✅ JSON converter

    @Autowired
    private WebApplicationContext webApplicationContext; // ✅ Application Context

    private Book book1;
    private Book book2;
    private List<Book> books;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc properly
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        book1 = new Book(1L, "Book One", "Author One", new BigDecimal("29.99"), LocalDate.of(2024, 1, 1));
        book2 = new Book(2L, "Book Two", "Author Two", new BigDecimal("39.99"), LocalDate.of(2024, 2, 1));
        books = Arrays.asList(book1, book2);
    }

    // ✅ Test for adding a book
    @Test
    void testAddBook() throws Exception {
        when(bookService.addBook(any(Book.class))).thenReturn(book1);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book One"))
                .andExpect(jsonPath("$.author").value("Author One"));
    }

    // ✅ Test for retrieving all books
    @Test
    void testGetAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Book One"))
                .andExpect(jsonPath("$[1].title").value("Book Two"));
    }

    // ✅ Test for retrieving a book by ID (Success)
    @Test
    void testGetBookById_Success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book1));

        mockMvc.perform(get("/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book One"));
    }

    // ✅ Test for retrieving a book by ID (Not Found)
    @Test
    void testGetBookById_NotFound() throws Exception {
        when(bookService.getBookById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ✅ Test for updating a book
    @Test
    void testUpdateBook() throws Exception {
        Book updatedBook = new Book(1L, "Updated Book", "Updated Author", new BigDecimal("49.99"), LocalDate.of(2025, 1, 1));

        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.author").value("Updated Author"));
    }

    // ✅ Test for deleting a book
    @Test
    void testDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }
}
