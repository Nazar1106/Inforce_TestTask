package com.example.booksscraper.mapper;
import com.example.booksscraper.dto.BookDTO;
import com.example.booksscraper.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book book(BookDTO bookDTO);

    BookDTO bookDTO(Book book);
}
