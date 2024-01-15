package com.finalprojectdaar.searchengine.controllers;

import com.finalprojectdaar.searchengine.models.Book;
import com.finalprojectdaar.searchengine.services.FileLookupService;
import com.finalprojectdaar.searchengine.services.OrderOutputService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@RestController()
@RequestMapping("/api/books")
public class BookStoreController {

    private final FileLookupService fileLookupService;
    private final OrderOutputService orderOutputService;
    private static final String IMAGE_DIRECTORY = "data/scrap-results/images/";

    public BookStoreController(FileLookupService fileLookupService, OrderOutputService orderOutputService) {
        this.fileLookupService = fileLookupService;
        this.orderOutputService = orderOutputService;
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<Book>> fetchBooks(@RequestParam String pattern, @RequestParam boolean isRegex) throws IOException {
        // TODO get this from request
        // OrderOutputService.OrderAlgorithm algorithm = OrderOutputService.OrderAlgorithm.PAGE_RANK;
        System.out.println("the dto is " + pattern + " " + isRegex);
        ArrayList<Integer> results = fileLookupService.getCandidate(pattern, isRegex);
        ArrayList<Book> books = orderOutputService.order(results, OrderOutputService.OrderAlgorithm.CENTRALITY);
        return ResponseEntity
                .status(200)
                .body(books);
    }

    @GetMapping("/img/{id}")
    public ResponseEntity<Resource> serveImage(@PathVariable String id) {
        try {
            // Construct the path to the image based on the id parameter
            Path imagePath = Paths.get(IMAGE_DIRECTORY).resolve(id + ".jpg");

            // Load the image as a resource
            Resource resource = new UrlResource(imagePath.toUri());

            // Check if the resource exists
            if (resource.exists() || resource.isReadable()) {
                // Set the Content-Type header based on the image type
                String contentType = "image/jpeg"; // You may need to adjust this based on your image types
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + resource.getFilename())
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException | RuntimeException e) {
            // Handle exceptions (e.g., file not found, malformed URL, etc.)
            return ResponseEntity.notFound().build();
        }
    }
}
