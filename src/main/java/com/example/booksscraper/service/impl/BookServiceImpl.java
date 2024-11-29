package com.example.booksscraper.service.impl;

import com.example.booksscraper.scraper.BookScraper;
import com.example.booksscraper.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class BookServiceImpl implements BookService {
    public static final int BATCH_SIZE = 100;
    public static final int PARAMETER_INDEX_ONE = 1;
    public static final int PARAMETER_INDEX_TWO = 2;
    public static final int PARAMETER_INDEX_THREE = 3;
    public static final int PARAMETER_INDEX_FOUR = 4;
    public static final String ERROR_DURING_BATCH_UPDATE_MSG = "Error during batch update: ";
    private final BookScraper bookScraper;
    private final JdbcTemplate jdbcTemplate;

    @Value("${scraper.start.url}")
    private String startUrl;

    @Autowired
    public BookServiceImpl(BookScraper bookScraper, JdbcTemplate jdbcTemplate) {
        this.bookScraper = bookScraper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Async
    @Override
    public CompletableFuture<Void> saveBooks() {
        return bookScraper.scrapeAllPages(startUrl).thenAccept(bookDTOs -> {
            String sql = "INSERT INTO defaultdb.books (price, title, availability, rating) VALUES (?, ?, ?, ?)";
            try {
                jdbcTemplate.batchUpdate(sql, bookDTOs, BATCH_SIZE, (ps, book) -> {
                    ps.setDouble(PARAMETER_INDEX_ONE, book.getPrice());
                    ps.setString(PARAMETER_INDEX_TWO, book.getTitle());
                    ps.setString(PARAMETER_INDEX_THREE, book.getAvailability());
                    ps.setString(PARAMETER_INDEX_FOUR, book.getRating());
                });
            } catch (DataAccessException e) {
                throw new RuntimeException(ERROR_DURING_BATCH_UPDATE_MSG + e);
            }
        });
    }
}