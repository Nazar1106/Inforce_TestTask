package com.example.booksscraper.controller;

import com.example.booksscraper.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/books")
    public ResponseEntity<String> saveBooks() {
        bookService.saveBooks();
        return ResponseEntity.status(HttpStatus.CREATED).body("Books saved successfully");
    }
}
