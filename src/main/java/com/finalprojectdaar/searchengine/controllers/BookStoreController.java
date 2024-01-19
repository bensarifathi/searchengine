package com.finalprojectdaar.searchengine.controllers;

import com.finalprojectdaar.searchengine.enumerations.OrderAlgorithm;
import com.finalprojectdaar.searchengine.models.Book;
import com.finalprojectdaar.searchengine.services.FileLookupService;
import com.finalprojectdaar.searchengine.services.OrderOutputService;
import com.finalprojectdaar.searchengine.services.RandomBookService;
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

@RestController()
@RequestMapping("/api/books")
public class BookStoreController {

    private final FileLookupService fileLookupService;
    private final OrderOutputService orderOutputService;
    private final RandomBookService randomBookService;

    public BookStoreController(
            FileLookupService fileLookupService, OrderOutputService orderOutputService,
            RandomBookService randomBookService
    ) {
        this.fileLookupService = fileLookupService;
        this.orderOutputService = orderOutputService;
        this.randomBookService = randomBookService;
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<Book>> fetchBooks(
            @RequestParam String pattern,
            @RequestParam boolean isRegex,
            @RequestParam OrderAlgorithm algorithm
    ) throws IOException {
        ArrayList<Integer> results = fileLookupService.getCandidate(pattern, isRegex);
        ArrayList<Book> books = orderOutputService.order(results, algorithm);
        return ResponseEntity
                .status(200)
                .body(books);
    }

    @GetMapping("/random")
    public ResponseEntity<ArrayList<Book>> randomBooks() throws IOException {
        ArrayList<Book> books = randomBookService.fetch();
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

            // Read the content of the text file into a string
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return "Error reading the text file";
        }
    }
}
