package com.example.booksscraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookDTO {
    private String title;
    private Double price;
    private String availability;
    private String rating;
}
