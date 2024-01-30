package com.finalprojectdaar.searchengine.controllers;

import com.finalprojectdaar.searchengine.enumerations.OrderAlgorithm;
import com.finalprojectdaar.searchengine.models.Book;
import com.finalprojectdaar.searchengine.services.BookService;
import com.finalprojectdaar.searchengine.services.FileLookupService;
import com.finalprojectdaar.searchengine.services.OrderOutputService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController()
@RequestMapping("/api/books")
public class BookStoreController {

    private final FileLookupService fileLookupService;
    private final OrderOutputService orderOutputService;
    private final BookService booksService;

    public BookStoreController(
            FileLookupService fileLookupService, OrderOutputService orderOutputService,
            BookService booksService
    ) {
        this.fileLookupService = fileLookupService;
        this.orderOutputService = orderOutputService;
        this.booksService = booksService;
    }

    @GetMapping("")
    public ResponseEntity<List<Book>> fetchBooks(
            @RequestParam String pattern,
            @RequestParam boolean isRegex,
            @RequestParam OrderAlgorithm algorithm
    ) throws IOException, ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        List<Integer> results = fileLookupService.getCandidate(pattern, isRegex);
        long endTime = System.currentTimeMillis();
        System.out.println("Time Taken to search while regex is " + isRegex + " is: " + (endTime - startTime));

        startTime = System.currentTimeMillis();
        List<Integer> orderedBookIds = orderOutputService.order(results, algorithm);
        endTime = System.currentTimeMillis();
        System.out.println("Time Taken to generate and order is: " + (endTime - startTime));

        List<Book> books = booksService.bookMapper(orderedBookIds);

        return ResponseEntity
                .status(200)
                .body(books);
    }

    @GetMapping("/random")
    public ResponseEntity<List<Book>> randomBooks() throws IOException {
        List<Book> books = booksService.randomBooks();
        return ResponseEntity
                .status(200)
                .body(books);
    }

    @GetMapping("/{bookId}/")
    public String getBookContent(@PathVariable Integer bookId) {
        try {
            // Load the text file as a resource
            String fileName = "books/" + bookId + ".txt";
            Resource resource = new ClassPathResource(fileName);

            booksService.clickOnBook(bookId);

            // Read the content of the text file into a string
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return "Error reading the text file";
        }
    }
}
