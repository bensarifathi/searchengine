package com.finalprojectdaar.searchengine.controllers;

import com.finalprojectdaar.searchengine.dto.BookQueryDto;
import com.finalprojectdaar.searchengine.models.Book;
import com.finalprojectdaar.searchengine.services.FileLookupService;
import com.finalprojectdaar.searchengine.services.OrderOutputService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController()
@RequestMapping("/api/books")
public class BookStoreController {

    private final FileLookupService fileLookupService;
    private final OrderOutputService orderOutputService;

    public BookStoreController(FileLookupService fileLookupService, OrderOutputService orderOutputService) {
        this.fileLookupService = fileLookupService;
        this.orderOutputService = orderOutputService;
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<Book>> fetchBooks(BookQueryDto bookQueryDto) throws IOException {
        System.out.println("am hitted");
        // TODO get this from request
        OrderOutputService.OrderAlgorithm algorithm = OrderOutputService.OrderAlgorithm.PAGE_RANK;
        ArrayList<Integer> results = fileLookupService.getCandidate(bookQueryDto.getPattern(), bookQueryDto.isRegex());
        ArrayList<Book> books = orderOutputService.order(results, algorithm);
        return ResponseEntity
                .status(200)
                .body(books);
    }
}
