package com.example.booksscraper.service;

import java.util.concurrent.CompletableFuture;

public interface BookService {
     CompletableFuture<Void> saveBooks();
}
